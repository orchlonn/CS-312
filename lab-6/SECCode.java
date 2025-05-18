/**
 * SECCode.java
 * 
 * Single Error‑Correcting (SEC) Code
 * 
 * Reads two "binary" files (text files containing only '0' and '1')
 * representing the transmitted and received bit‑streams (data+check bits),
 * computes whether a single‑bit error has occurred, and—if so—locates it.
 * 
 * Student:  [Your Name]
 * CWU ID:   [Your CWU ID]
 * Honor Code Statement: "I have not given or received unauthorized aid."
 */

 import java.io.*;
 import java.util.*;
 
 public class SECCode {
     public static void main(String[] args) {
         if (args.length != 2) {
             System.err.println("Usage: java SECCode <transmit-file> <receive-file>");
             System.exit(1);
         }
 
         String txFile = args[0];
         String rxFile = args[1];
 
         try {
             // Read transmitted file
             String txBits = readBitString(txFile);
             System.out.println("Transmitted file content: " + txBits);
 
             // Calculate M and K for transmitted file
             int totalBits = txBits.length();
             int k = computeK(totalBits);
             int m = totalBits - k;
             System.out.println("Total number of bytes read: " + totalBits + " bytes");
             System.out.println("M data bits is: " + m);
             System.out.println("K check bits is: " + k);
 
             // Display check bits for transmitted file
             List<Integer> checkPositions = getCheckBitPositions(k);
             List<Character> txCheckBits = extractBits(txBits, checkPositions);
             
             System.out.println("Location of the k check bits are: " + checkPositions);
             System.out.print("The k check bit values are: ");
             printBits(txCheckBits);
 
             // Read received file
             String rxBits = readBitString(rxFile);
             System.out.println("\nReceived file read: " + rxBits);
             int totalReceivedBits = rxBits.length();
             System.out.println("Total number of bytes read: " + totalReceivedBits + " bytes");
             
             // Compare file sizes
             if (txBits.length() != rxBits.length()) {
                System.out.println("Files are not the same size!");
                 System.exit(1);
             }
 
             // Display check bits for received file
             List<Character> rxCheckBits = extractBits(rxBits, checkPositions);

             System.out.println("Received file content: " + rxCheckBits);
 
             // Compute and display syndrome
             List<Character> syndrome = computeSyndrome(txCheckBits, rxCheckBits);
             System.out.print("Syndrome word: ");
             printBits(syndrome);
 
             // Interpret syndrome
             interpretSyndrome(syndrome);
 
         } catch (IOException e) {
             System.err.println("I/O error: " + e.getMessage());
             System.exit(1);
         }
     }
 
     private static void printBits(List<Character> bits) {
         for (char bit : bits) {
             System.out.print(bit);
         }
         System.out.println();
     }
 
     private static String readBitString(String filename) throws IOException {
         StringBuilder sb = new StringBuilder();
         try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
             int ch;
             while ((ch = br.read()) != -1) {
                 if (ch == '0' || ch == '1') sb.append((char) ch);
             }
         }
         return sb.toString();
     }
 
     private static int computeK(int totalBits) {
         int k = 1;
         while ((1 << k) < totalBits + 1) {
             k++;
         }
         return k;
     }
 
     private static List<Integer> getCheckBitPositions(int k) {
         List<Integer> positions = new ArrayList<>();
         for (int i = 0; i < k; i++) {
             positions.add((1 << i) - 1); // zero-based from right
         }
         return positions;
     }
 
     private static List<Character> extractBits(String bits, List<Integer> positions) {
         List<Character> list = new ArrayList<>();
         int n = bits.length();
         for (int p : positions) {
             list.add(bits.charAt(n - 1 - p));
         }
         return list;
     }
 
     private static List<Character> computeSyndrome(List<Character> tx, List<Character> rx) {
         List<Character> syn = new ArrayList<>();
         for (int i = 0; i < tx.size(); i++) {
             syn.add(tx.get(i) == rx.get(i) ? '0' : '1');
         }
         return syn;
     }
 
     private static void interpretSyndrome(List<Character> syndrome) {
         int ones = 0;
         for (char b : syndrome) if (b == '1') ones++;
 
         if (ones == 0) {
             System.out.println("No error detected.");
         } else if (ones == 1) {
             System.out.println("Single-bit error in a check bit; no data-bit correction needed.");
         } else {
             int pos = 0;
             for (int i = 0; i < syndrome.size(); i++) {
                 if (syndrome.get(i) == '1') pos |= (1 << i);
             }
             System.out.printf("Error detected at bit position %d (zero-based from right).%n", pos - 1);
         }
     }
 }
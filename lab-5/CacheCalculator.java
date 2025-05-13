// Name: Orchlon Chinbat
// Student ID: 50291063
// Honor code: By doing this assignment and submitting, I pledge that this submission is solely my own work, and it is not a copy or partial copy from anywhere.

import java.util.Scanner;

public class CacheCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input and calculate main memory size
        System.out.println("Main memory representation is A x 2^E");
        System.out.print("Value A: ");
        int A = scanner.nextInt();
        System.out.print("Exponent E: ");
        int E = scanner.nextInt();
        long memorySize = (long) A * (1L << E);
        System.out.println("Main memory: " + A + " x 2^" + E + " = " + memorySize + " bytes");

        // Input cache parameters
        System.out.print("Cache blocks: ");
        int cacheBlocks = scanner.nextInt();
        System.out.print("Bytes in Cache blocks: ");
        int bytesPerBlock = scanner.nextInt();
        System.out.print("k-set associative value: ");
        int k = scanner.nextInt();

        // Validate cache parameters are powers of 2
        if (!isPowerOfTwo(cacheBlocks)) {
            System.out.println("Cache blocks is not in Base 2, Program terminated");
            return;
        }
        if (!isPowerOfTwo(bytesPerBlock)) {
            System.out.println("Bytes in Cache blocks is not in Base 2, Program terminated");
            return;
        }
        if (!isPowerOfTwo(k)) {
            System.out.println("k-set associative value is not in Base 2, Program terminated");
            return;
        }

        // Input memory address
        System.out.print("Memory address in Base 16: ");
        String memoryAddressStr = scanner.next();

        // Validate memory address format
        if (!memoryAddressStr.startsWith("0x")) {
            System.out.println("Memory address must be in hexadecimal format starting with '0x'");
            return;
        }
        String hexAddress = memoryAddressStr.substring(2);
        if (!hexAddress.matches("[0-9A-Fa-f]+")) {
            System.out.println("Memory address is not in Base 16, Program terminated");
            return;
        }

        // Parse and validate memory address size
        long address;
        try {
            address = Long.parseLong(hexAddress, 16);
        } catch (NumberFormatException e) {
            System.out.println("Invalid hexadecimal memory address");
            return;
        }
        if (address >= memorySize) {
            System.out.println("Size of address is larger than main memory size, Program terminated");
            return;
        }

        // Calculate total bits for address
        long maxAddress = memorySize - 1;
        int totalBits = Long.toBinaryString(maxAddress).length();

        // Convert address to binary with padding
        String binaryAddress = Long.toBinaryString(address);
        int padding = totalBits - binaryAddress.length();
        if (padding > 0) {
            binaryAddress = "0".repeat(padding) + binaryAddress; // Requires Java 11+
            // Alternative for older Java: String.format("%" + totalBits + "s", binaryAddress).replace(' ', '0')
        }
        System.out.println("Address in binary: " + binaryAddress);

        // Direct Mapping
        int wordBitsDirect = Integer.numberOfTrailingZeros(bytesPerBlock);
        int lineBitsDirect = Integer.numberOfTrailingZeros(cacheBlocks);
        int tagBitsDirect = totalBits - lineBitsDirect - wordBitsDirect;
        String tagDirect = binaryAddress.substring(0, tagBitsDirect);
        String lineDirect = binaryAddress.substring(tagBitsDirect, tagBitsDirect + lineBitsDirect);
        String wordDirect = binaryAddress.substring(tagBitsDirect + lineBitsDirect);
        System.out.println("Direct Cache mapping of " + memoryAddressStr + " address");
        System.out.println("[TAG] " + tagBitsDirect + " : [LINE] " + lineBitsDirect + " : [WORD] " + wordBitsDirect);
        System.out.println("[TAG] " + tagDirect + " : [LINE] " + lineDirect + " : [WORD] " + wordDirect);

        // Associative Mapping
        int wordBitsAssoc = Integer.numberOfTrailingZeros(bytesPerBlock);
        int tagBitsAssoc = totalBits - wordBitsAssoc;
        String tagAssoc = binaryAddress.substring(0, tagBitsAssoc);
        String wordAssoc = binaryAddress.substring(tagBitsAssoc);
        System.out.println("Associative Cache mapping of " + memoryAddressStr + " address");
        System.out.println("[TAG] " + tagBitsAssoc + " : [WORD] " + wordBitsAssoc);
        System.out.println("[TAG] " + tagAssoc + " : [WORD] " + wordAssoc);

        // Set-Associative Mapping
        int numberOfSets = cacheBlocks / k;
        int setBits = Integer.numberOfTrailingZeros(numberOfSets);
        int wordBitsSetAssoc = Integer.numberOfTrailingZeros(bytesPerBlock);
        int tagBitsSetAssoc = totalBits - setBits - wordBitsSetAssoc;
        String tagSetAssoc = binaryAddress.substring(0, tagBitsSetAssoc);
        String setSetAssoc = binaryAddress.substring(tagBitsSetAssoc, tagBitsSetAssoc + setBits);
        String wordSetAssoc = binaryAddress.substring(tagBitsSetAssoc + setBits);
        System.out.println("Set-Associative Cache mapping of " + memoryAddressStr + " address");
        System.out.println("[TAG] " + tagBitsSetAssoc + " : [SET] " + setBits + " : [WORD] " + wordBitsSetAssoc);
        System.out.println("[TAG] " + tagSetAssoc + " : [SET] " + setSetAssoc + " : [WORD] " + wordSetAssoc);

        scanner.close();
    }

    // Helper method to check if a number is a power of 2
    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}
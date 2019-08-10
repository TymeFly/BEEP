package me.tymefly.beep.utils;

import javax.annotation.Nonnull;

import me.tymefly.beep.io.ProgrammerDriver;


/**
 * Helper class used to read the content of an EEPROM
 */
public class Reader {
    private static final int RADIX_HEX = 16;

    // Indexes into the each line generated by the programmer of key info
    private static final int ADDRESS_START = 0;
    private static final int ADDRESS_END = 4;
    private static final int HEX_START = 7;
    private static final int HEX_END = 56;

    private final short startAddress;
    private final short endAddress;
    private final int size;


    /**
     * Create a new instance of the reader
     * @param startAddress      lowest address to read
     * @param endAddress        highest address to read
     * @throws IllegalArgumentException if {@code endAddress} is before {@code startAddress}
     */
    public Reader(short startAddress, short endAddress) throws IllegalArgumentException {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.size = endAddress - startAddress + 1;

        Preconditions.checkArgument(size >= ADDRESS_START,
            "End address (0x%04x) is before start (0x%04x)", endAddress, startAddress);
    }


    /**
     * Read the EEPROM.
     * @return  The data in the EEPROM. The first byte in the array comes from the {@code startAddress}
     */
    @Nonnull
    public byte[] read() {
        boolean valid = true;
        boolean done = false;
        ProgrammerDriver driver = ProgrammerDriver.getInstance();
        String command = String.format("d%04x:%04x", startAddress, endAddress);
        byte[] buffer = new byte[size];

        driver.sendCommand(command);
        while (!done && valid) {
            String line = driver.readLine().trim();

            if (ProgrammerDriver.PROMPT.equals(line)) {
                done = true;
            } else if (ProgrammerDriver.FAIL.equals(line)) {
                valid = false;
            } else if (line.isEmpty()) {
                // Do nothing - this is formatting in the output
            } else {
                parseLine(buffer, line);
            }
        }

        return buffer;
    }


    private void parseLine(@Nonnull byte[] buffer, @Nonnull String line) {
        String address = line.substring(ADDRESS_START, ADDRESS_END);
        String[] elements = line.substring(HEX_START, HEX_END).trim().split(" +");
        short start = Short.parseShort(address, RADIX_HEX);
        short offset = (short) (start - startAddress);

        for (String element : elements) {
            short value = Short.parseShort(element, RADIX_HEX);
            buffer[offset++] = (byte) value;
        }
    }
}

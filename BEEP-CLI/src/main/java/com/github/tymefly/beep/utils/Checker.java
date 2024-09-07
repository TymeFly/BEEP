package com.github.tymefly.beep.utils;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.config.ExtendedConfig;


/**
 * Helper class used to check the content of the EEPROM matches some expected values
 */
public class Checker {
    private final short startAddress;
    private final short endAddress;
    private final int maxErrors;


    /**
     * Construct a Checker object that reports up to {@link ExtendedConfig#DEFAULT_MAX_ERRORS}
     * @param startAddress      Address of first byte to validate
     * @param endAddress        Address of last byte to validate
     */
    public Checker(short startAddress, short endAddress) {
        this(startAddress, endAddress, ExtendedConfig.DEFAULT_MAX_ERRORS);
    }


    /**
     * Constructor
     * @param startAddress          Address of first byte to validate
     * @param endAddress            Address of last byte to validate
     * @param maxErrorsDisplayed    Maximum number of errors that will be reported before giving up.
     */
    public Checker(short startAddress, short endAddress, int maxErrorsDisplayed) {
        Preconditions.checkArgument((startAddress <= endAddress),
            "Invalid range (0x%04x -> 0x%04x) for checking", startAddress, endAddress);

        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.maxErrors = maxErrorsDisplayed;
    }


    /**
     * Check the data in the {@code expected} array matches the content of the EEPROM
     * @param expected      The expected content of the EEPROM
     * @return              {@code true} only if the {@code expected} data matches the data in the EEPROM
     */
    public boolean check(@Nonnull byte[] expected) {
        byte[] actual = new Reader(startAddress, endAddress).read();
        boolean valid = (expected.length == actual.length);
        int cascade = maxErrors;

        if (!valid) {
            System.out.printf("ERROR: length mismatch. Expected %d bytes, but was %d%n",
                              expected.length, actual.length);
        } else {
            short offset = -1;

            while (++offset != expected.length) {
                boolean error = (expected[offset] == actual[offset]);
                valid &= error;

                if (!valid) {
                    if (cascade-- != 0) {
                        System.out.printf("ERROR: Address 0x%04x - Expected 0x%02x got 0x%02x%n",
                                          (startAddress + offset), expected[offset], actual[offset]);
                    } else {
                        System.out.println(".... more");
                        break;
                    }
                }
            }
        }

        if (valid) {
            System.out.println("EEPROM is valid");
        }

        return valid;
    }
}

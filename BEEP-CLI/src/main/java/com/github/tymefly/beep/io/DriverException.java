package com.github.tymefly.beep.io;

import javax.annotation.Nonnull;


/**
 * Unchecked exception for Driver errors
 */
public class DriverException extends RuntimeException {
    private static final long serialVersionUID = 0x01;


    /**
     * Constructor for a raw message
     * @param message       Human readable (raw) message
     */
    DriverException(@Nonnull String message) {
        super(message);
    }


    /**
     * Constructor for a formatted message
     * @param message       formatted message string
     * @param args          formatting arguments
     * @see java.util.Formatter
     */
    DriverException(@Nonnull String message, @Nonnull Object... args) {
        super(String.format(message, args));
    }


    /**
     * Constructor for a wrapped exception
     * @param message       Human readable (raw) message
     * @param cause         Wrapped exception
     */
    DriverException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
}


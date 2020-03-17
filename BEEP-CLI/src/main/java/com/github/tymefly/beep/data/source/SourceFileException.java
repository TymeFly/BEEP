package com.github.tymefly.beep.data.source;

import javax.annotation.Nonnull;


/**
 * Unchecked exception for SourceFiles
 */
public class SourceFileException extends RuntimeException {
    private static final long serialVersionUID = 0x01;


    /**
     * Constructor for a formatted message
     * @param message       formatted message string
     * @param args          formatting arguments
     * @see java.util.Formatter
     */
    SourceFileException(@Nonnull String message, @Nonnull Object... args) {
        super(String.format(message, args));
    }


    /**
     * Constructor for a wrapped exception
     * @param message       Human readable (raw) message
     * @param cause         Wrapped exception
     */
    SourceFileException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }
}


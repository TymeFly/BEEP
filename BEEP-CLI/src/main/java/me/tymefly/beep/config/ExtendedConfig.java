package me.tymefly.beep.config;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;


/**
 * Interface implemented by command specific config classes
 */
public interface ExtendedConfig {
    /** Lowest address in the EEPROM */
    int DEFAULT_BOTTOM_ADDRESS = 0;

    /** Highest address in the EEPROM */
    int DEFAULT_TOP_ADDRESS = 0x1fff;

    /** Default value for maximum number of errors to display */
    int DEFAULT_MAX_ERRORS = 10;


    /**
     * Returns the implementing command
     * @return the implementing command
     */
    @Nonnull
    Command getCommand();
}

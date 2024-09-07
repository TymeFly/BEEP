package com.github.tymefly.beep.config;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.command.Command;
import com.github.tymefly.beep.command.Erase;

/**
 * Configuration associated with the "Erase" command
 */
public class EraseConfig implements ExtendedConfig {
    @Nonnull
    @Override
    public Command getCommand() {
        return new Erase();
    }
}

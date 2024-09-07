package com.github.tymefly.beep.config;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.command.Command;
import com.github.tymefly.beep.command.Test;


/**
 * Configuration associated with the "Test" command
 */
public class TestConfig implements ExtendedConfig {
    @Nonnull
    @Override
    public Command getCommand() {
        return new Test();
    }
}

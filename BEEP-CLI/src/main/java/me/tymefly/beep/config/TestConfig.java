package me.tymefly.beep.config;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Test;


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

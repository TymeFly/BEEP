package com.github.tymefly.beep.config;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.command.Command;
import com.github.tymefly.beep.command.Ping;

/**
 * Configuration associated with the "Ping" command
 */
public class PingConfig implements ExtendedConfig {
    @Nonnull
    @Override
    public Command getCommand() {
        return new Ping();
    }
}

package me.tymefly.beep.config;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Ping;

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

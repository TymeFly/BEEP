package me.tymefly.beep.config;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Erase;

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

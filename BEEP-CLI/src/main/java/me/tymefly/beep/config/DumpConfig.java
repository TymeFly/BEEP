package me.tymefly.beep.config;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Dump;
import me.tymefly.beep.config.handler.AddressOptionHandler;
import org.kohsuke.args4j.Option;

/**
 * Configuration associated with the "Dump" command
 */
public class DumpConfig implements ExtendedConfig {
    @Option(name = "--start", handler = AddressOptionHandler.class, usage = "First address of first byte to dump")
    private short start = DEFAULT_BOTTOM_ADDRESS;

    @Option(name = "--end", handler = AddressOptionHandler.class, usage = "First address of last byte to dump")
    private short end = DEFAULT_TOP_ADDRESS;


    @Nonnull
    @Override
    public Command getCommand() {
        return new Dump(this);
    }


    /**
     * Returns the first address to dump
     * @return the first address to dump
     */
    public short getStart() {
        return start;
    }


    /**
     * Returns the last address to dump
     * @return the last address to dump
     */
    public short getEnd() {
        return end;
    }
}

package me.tymefly.beep.config;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Fill;
import me.tymefly.beep.config.handler.AddressOptionHandler;
import me.tymefly.beep.config.handler.ByteOptionHandler;
import org.kohsuke.args4j.Option;

/**
 * Configuration associated with the "Fill" command
 */
public class FillConfig implements ExtendedConfig {
    private static final int DEFAULT_FILL = 0xff;

    @Option(name = "--start", handler = AddressOptionHandler.class, usage = "First address of first byte to fill")
    private short start = DEFAULT_BOTTOM_ADDRESS;

    @Option(name = "--end", handler = AddressOptionHandler.class, usage = "First address of last byte to fill")
    private short end = DEFAULT_TOP_ADDRESS;

    @Option(name = "--byte",
            handler = ByteOptionHandler.class,
            usage = "Value of byte to fill. Default is " + DEFAULT_FILL)
    private byte fill = (byte) DEFAULT_FILL;


    @Nonnull
    @Override
    public Command getCommand() {
        return new Fill(this);
    }


    /**
     * Returns the first address to write
     * @return the first address to write
     */
    public short getStart() {
        return start;
    }


    /**
     * Returns the last address to write
     * @return the last address to write
     */
    public short getEnd() {
        return end;
    }


    /**
     * Returns the byte to fill the EEPROM with
     * @return the byte to fill the EEPROM with
     */
    public byte getFill() {
        return fill;
    }
}

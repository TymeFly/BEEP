package com.github.tymefly.beep.command;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.config.DumpConfig;
import com.github.tymefly.beep.io.ProgrammerDriver;
import com.github.tymefly.beep.utils.Preconditions;


/**
 * Dump data in the EEPROM to Standard out. The format of the data comes from the embedded software, but
 * is in the format
 * <pre>{@code
 *  {address}: {hex bytes}....     {ascii}...
 * }</pre>
 */
public class Dump implements Command {
    private final short startAddress;
    private final short endAddress;


    /**
     * Create an instance of the "Dump" command handler
     * @param config        Command configuration
     */
    public Dump(@Nonnull DumpConfig config) {
        this(config.getStart(), config.getEnd());
    }


    /**
     * Create an instance of the "Dump" command handler
     * @param startAddress      First address to dump
     * @param endAddress        Last address to dump
     */
    public Dump(short startAddress, short endAddress) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }


    @Override
    public boolean execute() {
        Preconditions.checkArgument((startAddress <= endAddress),
            "Invalid range (0x%04x -> 0x%04x) for checking", startAddress, endAddress);

        ProgrammerDriver driver = ProgrammerDriver.getInstance();
        String command = String.format("d%04x:%04x", startAddress, endAddress);

        driver.sendCommand(command);
        driver.dumpResponse();

        return true;
    }
}

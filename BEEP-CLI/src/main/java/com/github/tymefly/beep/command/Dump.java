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
    private DumpConfig config;


    /**
     * Create an instance of the "Dump" command handler
     * @param config        Command configuration
     */
    public Dump(@Nonnull DumpConfig config) {
        this.config = config;
    }


    @Override
    public boolean execute() {
        short startAddress = config.getStart();
        short endAddress = config.getEnd();

        Preconditions.checkArgument((startAddress <= endAddress),
            "Invalid range (0x%04x -> 0x%04x) for checking", startAddress, endAddress);

        ProgrammerDriver driver = ProgrammerDriver.getInstance();
        String command = String.format("d%04x:%04x", startAddress, endAddress);

        driver.sendCommand(command);
        driver.dumpResponse();

        return true;
    }
}

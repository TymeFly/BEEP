package me.tymefly.beep.command;

import javax.annotation.Nonnull;

import me.tymefly.beep.config.FillConfig;
import me.tymefly.beep.io.ProgrammerDriver;
import me.tymefly.beep.utils.Preconditions;


/**
 * Write a byte of all locations between the start and end address
 */
public class Fill implements Command {
    private FillConfig config;

    /**
     * Create an instance of the "Fill" command handler
     * @param config        Command configuration
     */
    public Fill(@Nonnull FillConfig config) {
        this.config = config;
    }


    @Override
    public boolean execute() {
        short startAddress = config.getStart();
        short endAddress = config.getEnd();

        Preconditions.checkArgument((startAddress <= endAddress),
            "Invalid range (0x%04x -> 0x%04x) for checking", startAddress, endAddress);

        ProgrammerDriver driver = ProgrammerDriver.getInstance();
        String command = String.format("f%04x:%04x:%02x", startAddress, endAddress, config.getFill());

        driver.sendCommand(command);
        driver.dumpResponse();

        return true;
    }
}

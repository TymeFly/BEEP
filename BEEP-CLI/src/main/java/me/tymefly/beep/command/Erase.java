package me.tymefly.beep.command;

import me.tymefly.beep.io.ProgrammerDriver;


/**
 * Erase the EEPROM by writing {@literal 0xff} to all memory locations
 */
public class Erase implements Command {
    @Override
    public boolean execute() {
        ProgrammerDriver driver = ProgrammerDriver.getInstance();

        driver.sendCommand("e");
        driver.dumpResponse();

        return true;
    }
}

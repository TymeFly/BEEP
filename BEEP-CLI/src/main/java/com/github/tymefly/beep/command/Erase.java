package com.github.tymefly.beep.command;

import com.github.tymefly.beep.io.ProgrammerDriver;


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

package com.github.tymefly.beep.command;

import com.github.tymefly.beep.io.ProgrammerDriver;


/**
 * Instruct the programmer to test the EEPROM
 */
public class Test implements Command {
    @Override
    public boolean execute() {
        ProgrammerDriver driver = ProgrammerDriver.getInstance();

        driver.sendCommand("t");
        driver.dumpResponse();

        return true;
    }
}

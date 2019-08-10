package me.tymefly.beep.command;

import me.tymefly.beep.io.ProgrammerDriver;


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

package com.github.tymefly.beep.command;

import com.github.tymefly.beep.io.ProgrammerDriver;


/**
 * "Ping" the programmer to see if it's still there
 */
public class Ping implements Command {
    @Override
    public boolean execute() {
        ProgrammerDriver driver = ProgrammerDriver.getInstance();

        driver.sendCommand("-");
        driver.dumpResponse();

        return true;
    }
}

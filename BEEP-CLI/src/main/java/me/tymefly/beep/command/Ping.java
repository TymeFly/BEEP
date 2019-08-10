package me.tymefly.beep.command;

import me.tymefly.beep.io.ProgrammerDriver;


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

package com.github.tymefly.beep;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.config.CliParser;
import com.github.tymefly.beep.io.ProgrammerDriver;


/**
 * Application entry point
 */
public class Main {
    private Main() {
    }


    /**
     * Application entry point
     * @param args          Command line arguments
     */
    public static void main(String[] args) {
        CliParser config = CliParser.parse(Main.class, args);
        boolean done;

        if (config.requestHelp()) {
            config.displayUsage();
            done = true;
        } else if (config.isValid()) {
            done = run(config);
        } else {
            done = false;
        }

        System.exit(done ? 0 : 1);
    }


    private static boolean run(@Nonnull CliParser config) {
        boolean done;

        try {
            done = config.getCommand().execute();
        } catch (Exception e) {
            System.err.println("**** ERROR ****");
            e.printStackTrace();
            done = false;
        } finally {
            ProgrammerDriver.getInstance().close();
        }

        return done;
    }
}

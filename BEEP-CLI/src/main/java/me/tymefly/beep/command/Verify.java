package me.tymefly.beep.command;

import javax.annotation.Nonnull;

import me.tymefly.beep.config.VerifyConfig;
import me.tymefly.srec.SReader;
import me.tymefly.beep.utils.Checker;


/**
 * Read the contents of the EEPROM and verify that it matches a particular file
 */
public class Verify implements Command {
    private final VerifyConfig config;

    /**
     * Create an instance of the "Verify" command handler
     * @param config        Command configuration
     */
    public Verify(@Nonnull VerifyConfig config) {
        this.config = config;
    }


    @Override
    public boolean execute() {
        System.out.printf("Checking against file %s%n", config.getSource().getAbsolutePath());

        short maxErrors = config.getMaxErrors();
        SReader reader = SReader.load(config.getSource());
        byte[] expected = reader.getData();
        short startAddress = reader.getStartAddress();
        short endAddress = reader.getEndAddress();

        dumpHeaders(reader);

        Checker checker = new Checker(startAddress, endAddress, maxErrors);

        return checker.check(expected);
    }


    private void dumpHeaders(@Nonnull SReader reader) {
        if (config.showHeaders()) {
            for (String header : reader.getHeaders()) {
                System.out.printf("   Header: %s%n", header);
            }
        }
    }
}

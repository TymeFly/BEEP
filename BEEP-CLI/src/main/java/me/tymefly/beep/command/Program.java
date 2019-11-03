package me.tymefly.beep.command;

import javax.annotation.Nonnull;

import me.tymefly.beep.config.ProgramConfig;
import me.tymefly.beep.data.source.SourceFile;
import me.tymefly.beep.data.source.SourceFileManager;
import me.tymefly.beep.io.ProgrammerDriver;
import me.tymefly.beep.utils.Checker;


/**
 * Read an data file from the local file system and send the contents to the EEPROM
 */
public class Program implements Command {
    private static final int FRAME_SIZE = Math.min(0x30, ProgrammerDriver.BUFFER_SIZE);
    private static final int FRAME_COUNT = 16;

    private final ProgramConfig config;

    /**
     * Create an instance of the "Program" command handler
     * @param config        Command configuration
     */
    public Program(@Nonnull ProgramConfig config) {
        this.config = config;
    }


    @Override
    public boolean execute() {
        System.out.printf("Reading file %s%n", config.getSource().getAbsolutePath());

        SourceFile reader = SourceFileManager.load(config.getSource());
        int size = reader.size();
        boolean success;

        dumpHeaders(reader);
        dumpStats(reader);

        if (size == 0) {
            success = true;
        } else {
            success = program(reader) && validate(reader);
        }

        if (success) {
            System.out.println("Success");
        }

        return success;
    }


    private void dumpStats(@Nonnull SourceFile reader) {
        System.out.printf("Start Address: 0x%04x%n", reader.getStartAddress());
        System.out.printf("End Address  : 0x%04x%n", reader.getEndAddress());
        System.out.printf("Size         : %d%n", reader.size());
    }


    private void dumpHeaders(@Nonnull SourceFile reader) {
        if (config.showHeaders()) {
            for (String header : reader.getHeaders()) {
                System.out.printf("   Header: %s%n", header);
            }
        }
    }


    private boolean program(@Nonnull SourceFile reader) {
        short startAddress = reader.getStartAddress();
        byte[] buffer = reader.getData();
        int size = buffer.length;
        boolean success = true;
        String command = String.format("p%04x,%02x", startAddress, buffer.length);
        ProgrammerDriver driver = ProgrammerDriver.getInstance();

        driver.sendCommand(command);
        driver.readLine();                                          // Programming message

        int progress = 1;
        int offset = 0;
        while (size > 0) {
            if (--progress == 0) {
                System.out.printf("%nWriting to 0x%04x  ", reader.getStartAddress() + offset);
                progress = FRAME_COUNT;
            }

            System.out.print('.');

            int blockSize = Math.min(FRAME_SIZE, size);
            driver.sendData(buffer, offset, blockSize);
            size -= blockSize;
            offset += blockSize;

            String response = driver.readLine();
            String expected = "*" + size;

            success = expected.equals(response);
            if (!success) {
                System.out.println();
                System.out.println("ERROR: Unexpected response from Programmer");
                System.out.println("  Expected : " + expected);
                System.out.println("  Actual   : " + response);

                break;
            }
        }

        return success;
    }


    private boolean validate(@Nonnull SourceFile reader) {
        ProgrammerDriver driver = ProgrammerDriver.getInstance();

        driver.dumpResponse();

        System.out.println();
        System.out.println("Checking");

        Checker checker = new Checker(reader.getStartAddress(), reader.getEndAddress());
        boolean success = checker.check(reader.getData());

        System.out.println();

        return success;
    }
}

package me.tymefly.beep.command;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

import me.tymefly.beep.config.UploadConfig;
import me.tymefly.beep.utils.Preconditions;
import me.tymefly.beep.utils.Reader;
import me.tymefly.srec.SWriter;


/**
 * Upload the contents of an EEPROM and save save them to an SRecord file.
 */
public class Upload implements Command {
    private static final int BLOCK_SIZE = 32;
    private static final DateTimeFormatter HOUR_MINUTE = DateTimeFormatter.ofPattern("HH:mm");

    private final UploadConfig config;

    /**
     * Create an instance of the "Save" command handler
     * @param config        Command configuration
     */
    public Upload(@Nonnull UploadConfig config) {
        this.config = config;
    }


    @Override
    public boolean execute() {
        short startAddress = config.getStart();
        short endAddress = config.getEnd();

        Preconditions.checkArgument((startAddress <= endAddress),
            "Invalid range (0x%04x -> 0x%04x) for checking", startAddress, endAddress);

        System.out.printf("Generating file %s%n", config.getDestination().getAbsolutePath());
        String description = "Uploaded on " + LocalDate.now() + " at " + LocalTime.now().format(HOUR_MINUTE);

        byte[] raw = new Reader(startAddress, endAddress).read();

        try (
            SWriter writer = new SWriter(config.getDestination())
        ) {
            for (String header : config.getHeaders()) {
                writer.withHeader(header);
            }

            writer.withHeader(description);

            int size = config.getEnd() - config.getStart() + 1;
            int blocks = (size + BLOCK_SIZE - 1) / BLOCK_SIZE;              // Round fractional blocks up
            int offset = 0;
            int address = config.getStart();
            while (blocks-- != 0) {
                int length = Math.min((size - offset), BLOCK_SIZE);

                writer.withData(address, raw, offset, length);
                address += BLOCK_SIZE;
                offset += BLOCK_SIZE;
            }
        }

        return true;
    }
}

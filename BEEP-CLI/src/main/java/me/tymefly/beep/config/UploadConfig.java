package me.tymefly.beep.config;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Upload;
import me.tymefly.beep.config.handler.AddressOptionHandler;
import me.tymefly.beep.config.handler.WellBehavedStringArrayOptionHandler;
import org.kohsuke.args4j.Option;

/**
 * Configuration associated with the "Upload" command
 */
public class UploadConfig implements ExtendedConfig {
    @Option(name = "--out", usage = "Generated S-Record file", required = true)
    private File destination;

    @Option(name = "--start", handler = AddressOptionHandler.class, usage = "First address of first byte to read")
    private short start = DEFAULT_BOTTOM_ADDRESS;

    @Option(name = "--end", handler = AddressOptionHandler.class, usage = "First address of last byte to read")
    private short end = DEFAULT_TOP_ADDRESS;

    @Option(name = "--header", handler = WellBehavedStringArrayOptionHandler.class, usage = "SRecord file headers")
    private List<String> headers;



    @Nonnull
    @Override
    public Command getCommand() {
        return new Upload(this);
    }


    /**
     * Returns the file that the EEPROM contents will be written to.
     * @return the file that the EEPROM contents will be written to.
     */
    @Nonnull
    public File getDestination() {
        return destination;
    }

    /**
     * Returns the first address to upload
     * @return the first address to upload
     */
    public short getStart() {
        return start;
    }


    /**
     * Returns the last address to upload
     * @return the last address to upload
     */
    public short getEnd() {
        return end;
    }


    /**
     * Returns all of the headers to be added to the generated SRecord file
     * @return all of the headers to be added to the generated SRecord file
     */
    @Nonnull
    public List<String> getHeaders() {
        return (headers == null ? Collections.emptyList() : headers);
    }
}

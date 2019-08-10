package me.tymefly.beep.config;

import java.io.File;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.command.Verify;
import org.kohsuke.args4j.Option;

/**
 * Configuration associated with the "Verify" command
 */
public class VerifyConfig implements ExtendedConfig {
    @Option(name = "--file", usage = "S-Record file", required = true)
    private File source;

    @Option(name = "--hideHeaders", usage = "Don't show headers")
    private boolean hideHeaders = false;

    @Option(name = "--maxErrors", usage = "Maximum number of errors to display")
    private short maxErrors = DEFAULT_MAX_ERRORS;


    @Nonnull
    @Override
    public Command getCommand() {
        return new Verify(this);
    }


    /**
     * Returns SRecord file to read
     * @return SRecord file to read
     */
    @Nonnull
    public File getSource() {
        return source;
    }


    /**
     * Returns {@literal true} only if the headers in the source file should be displayed
     * @return {@literal true} only if the headers in the source file should be displayed
     */
    public boolean showHeaders() {
        return !hideHeaders;
    }


    /**
     * Returns the maximum number of errors to display before giving up
     * @return the maximum number of errors to display before giving up
     */
    public short getMaxErrors() {
        return maxErrors;
    }
}

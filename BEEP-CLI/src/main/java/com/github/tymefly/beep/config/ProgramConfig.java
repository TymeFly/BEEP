package com.github.tymefly.beep.config;

import java.io.File;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.command.Command;
import com.github.tymefly.beep.command.Program;
import org.kohsuke.args4j.Option;

/**
 * Configuration associated with the "Program" command
 */
public class ProgramConfig implements ExtendedConfig {
    @Option(name = "--file", usage = "S-Record file", required = true)
    private File source;

    @Option(name = "--hideHeaders", usage = "Don't show headers")
    private boolean hideHeaders = false;


    @Nonnull
    @Override
    public Command getCommand() {
        return new Program(this);
    }


    /**
     * Returns data file to read
     * @return data file to read
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
}

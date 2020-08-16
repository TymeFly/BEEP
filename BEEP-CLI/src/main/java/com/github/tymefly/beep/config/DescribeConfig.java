package com.github.tymefly.beep.config;

import java.io.File;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.command.Command;
import com.github.tymefly.beep.command.Describe;
import org.kohsuke.args4j.Option;


/**
 * Configuration associated with the "describe" command
 */
public class DescribeConfig implements ExtendedConfig {
    @Option(name = "--file", usage = "S-Record file", required = true)
    private File source;

    /**
     * Returns data file to read
     * @return data file to read
     */
    @Nonnull
    public File getSource() {
        return source;
    }


    @Nonnull
    @Override
    public Command getCommand() {
        return new Describe(this);
    }
}

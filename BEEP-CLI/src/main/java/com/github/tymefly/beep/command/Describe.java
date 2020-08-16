package com.github.tymefly.beep.command;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.config.DescribeConfig;
import com.github.tymefly.beep.data.source.SourceFile;
import com.github.tymefly.beep.data.source.SourceFileManager;


/**
 * Dump the metadata from an SREC file
 */
public class Describe implements Command {
    private final DescribeConfig config;

    /**
     * Create an instance of the "Program" command handler
     * @param config        Command configuration
     */
    public Describe(@Nonnull DescribeConfig config) {
        this.config = config;
    }


    @Override
    public boolean requiresProgrammer() {
        return false;
    }


    @Override
    public boolean execute() {
        System.out.printf("Reading file %s%n", config.getSource().getAbsolutePath());

        SourceFile reader = SourceFileManager.load(config.getSource());

        Metadata.dumpStats(reader);
        Metadata.dumpHeaders(reader);

        return true;
    }
}

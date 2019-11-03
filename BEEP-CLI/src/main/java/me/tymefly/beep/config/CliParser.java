package me.tymefly.beep.config;

import java.io.PrintStream;

import javax.annotation.Nonnull;

import me.tymefly.beep.command.Command;
import me.tymefly.beep.utils.Preconditions;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.SubCommand;
import org.kohsuke.args4j.spi.SubCommandHandler;
import org.kohsuke.args4j.spi.SubCommands;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;


/**
 * Command Line Argument parser.
 */
public class CliParser {
    private static final int SCREEN_WIDTH_CHARACTERS = 80;
    private static final int DEFAULT_READ_TIMEOUT_MS = 1_000;
    private static final int DEFAULT_WRITE_TIMEOUT_MS = 100;
    private static final int DEFAULT_PROGRAMMER_TIMEOUT_MS = 10_000;

    // TODO: an option to set the size of the EEPROM
    private static CliParser instance;

    @Option(name = "-p", aliases = "--port", required = true, usage = "COM port programmer is connected to")
    private byte port;

    @Option(name = "--readTimeout", usage = "read timeout in milliseconds when waiting reading the COM port")
    private int readTimeoutMs = DEFAULT_READ_TIMEOUT_MS;

    @Option(name = "--writeTimeout", usage = "write timeout in milliseconds when waiting reading the COM port")
    private int writeTimeoutMs = DEFAULT_WRITE_TIMEOUT_MS;

    @Option(name = "--programmerTimeout", usage = "timeout in milliseconds when waiting for programmer to respond")
    private int programmerTimeoutMs = DEFAULT_PROGRAMMER_TIMEOUT_MS;

    @Option(name = "-?", aliases = {"--help", "-h"}, help = true)
    private boolean help;

    @Argument(required = true,
              metaVar = "action",
              usage = "subcommands: ping, test, dump, fill, erase, upload, program, verify}",
              handler = SubCommandHandler.class)
    @SubCommands({
            @SubCommand(name = "dump", impl = DumpConfig.class),
            @SubCommand(name = "fill", impl = FillConfig.class),
            @SubCommand(name = "ping", impl = PingConfig.class),
            @SubCommand(name = "erase", impl = EraseConfig.class),
            @SubCommand(name = "test", impl = TestConfig.class),
            @SubCommand(name = "program", impl = ProgramConfig.class),
            @SubCommand(name = "verify", impl = VerifyConfig.class),
            @SubCommand(name = "upload", impl = UploadConfig.class)})
    private ExtendedConfig extendedConfig;

    private final String parent;
    private final CmdLineParser parser;
    private boolean isValid;


    private CliParser(@Nonnull Class<?> parent) {
        ParserProperties parserProperties = ParserProperties.defaults()
                .withUsageWidth(SCREEN_WIDTH_CHARACTERS)
                .withAtSyntax(false)
                .withShowDefaults(true);

        this.parent = parent.getName();
        this.parser = new CmdLineParser(this, parserProperties);
    }


    /**
     * Parse the command line arguments. This must be done before calling any other methods in this class
     * @param parent        The class that implements {@literal main(String[])}
     * @param args          The command line arguments
     * @return              The singleton instance of this class
     */
    @Nonnull
    public static CliParser parse(@Nonnull Class<?> parent, String... args) {
        Preconditions.checkState(instance == null, "Command line has already been set");

        instance = new CliParser(parent);
        instance.parse(args);

        return instance;
    }


    private void parse(String... args) {
        try {
            parser.parseArgument(args);
            isValid = true;
        } catch (CmdLineException e) {
            isValid = false;

            System.err.println("Error: " + e.getMessage());
            System.err.println();
            displayUsage(System.err);
        }
    }


    /**
     * Returns the singleton instance of this class.
     * This is only valid if the command line has been parsed
     * @return the singleton instance of this class
     * @see #parse(Class, String...)
     */
    @Nonnull
    public static CliParser getInstance() {
        Preconditions.checkState(instance != null, "Command line has not been set");

        return instance;
    }


    /**
     * Dumps the command line syntax to {@link System#out}
     */
    public void displayUsage() {
        displayUsage(System.out);
    }


    private void displayUsage(@Nonnull PrintStream stream) {
        stream.println("Usage:");
        stream.println("  java " + parent + " " + parser.printExample(ALL));
        stream.println();

        this.parser.printUsage(stream);
    }


    /**
     * Returns {@literal true} only of the command line was valid
     * @return {@literal true} only of the command line was valid
     */
    public boolean isValid() {
        return isValid;
    }


    /**
     * Returns the COM port the programmer is attached to
     * @return the COM port the programmer is attached to
     */
    public byte getPort() {
        return port;
    }


    /**
     * Returns {@literal true} only if the user asked to see the help page
     * @return {@literal true} only if the user asked to see the help page
     */
    public boolean requestHelp() {
        return help;
    }


    /**
     * Returns the configured command given in the CLI
     * @return the configured command given in the CLI
     */
    @Nonnull
    public Command getCommand() {
        return extendedConfig.getCommand();
    }


    /**
     * Returns the COM port read timeout in milliseconds
     * @return the COM port read timeout in milliseconds
     */
    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }


    /**
     * Returns the COM port write timeout in milliseconds
     * @return the COM port write timeout in milliseconds
     */
    public int getWriteTimeoutMs() {
        return writeTimeoutMs;
    }

    /**
     * Returns programmer timeout in milliseconds
     * @return programmer timeout in milliseconds
     */
    public int getProgrammerTimeoutMs() {
        return programmerTimeoutMs;
    }
}

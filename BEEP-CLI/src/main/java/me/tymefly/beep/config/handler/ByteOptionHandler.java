package me.tymefly.beep.config.handler;

import javax.annotation.Nonnull;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

/**
 * Options handler that will read a data byte in hex
 */
public class ByteOptionHandler extends OneArgumentOptionHandler<Byte> {
    private static final String HEX_PATTERN = "(?i)0x[0-9a-f]{1,2}";
    private static final String DEC_PATTERN = "\\d{1,3}";
    private static final int MAX_BYTE = 0xff;
    private static final int RADIX_HEX = 16;


    /**
     * Constructor for handlers that read byte values in hex
     * @param parser        Standard args4j parameter
     * @param option        Standard args4j parameter
     * @param setter        Standard args4j parameter
     */
    public ByteOptionHandler(@Nonnull CmdLineParser parser,
                             @Nonnull OptionDef option,
                             @Nonnull Setter<Byte> setter) {
        super(parser, option, setter);
    }


    @Override
    protected Byte parse(@Nonnull String argument) throws NumberFormatException, CmdLineException {
        short value;

        if (argument.matches(HEX_PATTERN)) {
            value = Short.parseShort(argument.substring(2), RADIX_HEX);
        } else if (argument.matches(DEC_PATTERN)) {
            value = Short.parseShort(argument);
        } else {
            throw new CmdLineException("Malformed byte '" + argument + "'");
        }

        if (value > MAX_BYTE) {
            throw new CmdLineException("byte '" + argument + "' is out of range");
        }

        return (byte) value;
    }
}

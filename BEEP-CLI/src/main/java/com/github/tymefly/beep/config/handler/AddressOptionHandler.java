package com.github.tymefly.beep.config.handler;

import javax.annotation.Nonnull;

import com.github.tymefly.beep.config.ExtendedConfig;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;


/**
 * Options handler that will read an address in hex
 */
public class AddressOptionHandler extends OneArgumentOptionHandler<Short> {
    private static final String HEX_PATTERN = "(?i)0x[0-9a-f]{1,4}";
    private static final String DEC_PATTERN = "\\d{1,4}";
    private static final int RADIX_HEX = 16;


    /**
     * Constructor for handlers that read hex addresses
     * @param parser        Standard args4j parameter
     * @param option        Standard args4j parameter
     * @param setter        Standard args4j parameter
     */
    public AddressOptionHandler(@Nonnull CmdLineParser parser,
                                @Nonnull OptionDef option,
                                @Nonnull Setter<Short> setter) {
        super(parser, option, setter);
    }


    @Override
    protected Short parse(@Nonnull String argument) throws NumberFormatException, CmdLineException {
        short value;

        if (argument.matches(HEX_PATTERN)) {
            value = Short.parseShort(argument.substring(2), RADIX_HEX);
        } else if (argument.matches(DEC_PATTERN)) {
            value = Short.parseShort(argument);
        } else {
            throw new CmdLineException("Malformed address '" + argument + "'");
        }

        if ((value > ExtendedConfig.DEFAULT_TOP_ADDRESS) || (value < 0)) {
            throw new CmdLineException("address '" + argument + "' is out of range");
        }

        return value;
    }
}

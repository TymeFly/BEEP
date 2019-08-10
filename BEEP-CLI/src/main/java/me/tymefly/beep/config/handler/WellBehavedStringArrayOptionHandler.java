package me.tymefly.beep.config.handler;

import javax.annotation.Nonnull;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Messages;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;



/**
 * Handler for reading strings that may contain spaces
 * @see <a href="https://stackoverflow.com/questions/29964638/args4j-list-arguments-that-contain-space-not-handled">
 *          Stackoverflow
 *      </a>
 */
public class WellBehavedStringArrayOptionHandler extends OptionHandler<String> {
    /**
     * Constructor for handlers that read strings with spaces
     * @param parser        Standard args4j parameter
     * @param option        Standard args4j parameter
     * @param setter        Standard args4j parameter
     */
    public WellBehavedStringArrayOptionHandler(@Nonnull CmdLineParser parser,
                                               @Nonnull OptionDef option,
                                               @Nonnull Setter<String> setter) {
        super(parser, option, setter);
    }


    @Override
    public String getDefaultMetaVariable() {
        return Messages.DEFAULT_META_STRING_ARRAY_OPTION_HANDLER.format();
    }


    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        int counter;
        for (counter = 0; counter < params.size(); counter++) {
            String param = params.getParameter(counter);
            if (param.startsWith("-")) {
                break;
            }
            setter.addValue(param);
        }

        return counter;
    }
}
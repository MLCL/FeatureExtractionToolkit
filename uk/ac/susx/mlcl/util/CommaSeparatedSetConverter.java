/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.util;

import com.beust.jcommander.IStringConverter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Convert a string of comma separated words into a set of strings. For use with
 * the JCommander command-line parsing library.
 *
 * @author Hamish Morgan (hamish.morgan@sussex.ac.uk)
 * @version 13-09-2011
 */
/**
 *
 * @author hiam20
 */
public final class CommaSeparatedSetConverter implements IStringConverter<Set<String>> {

    private static final Logger LOG = Logger.getLogger(CommaSeparatedSetConverter.class.getName());

    private static final Pattern COMMA_SPLIT_REGEX = Pattern.compile(",");

    /**
     * Perform the conversion from the command-line parameter value string to a
     * set of strings. Note that order is lost, and duplicates will be ignored
     * with a WARNING level log message.
     *
     * @param value the command line parameter value string
     * @return an set of strings created from the parameter value.
     */
    @Override
    public Set<String> convert(final String value) {
        if (value == null)
            throw new NullPointerException("Argument value is null.");
        final String[] items = COMMA_SPLIT_REGEX.split(value);
        final Set<String> result = new HashSet<String>(items.length);
        for (String item : items) {
            if (!result.add(item) && LOG.isLoggable(Level.WARNING))
                LOG.log(Level.WARNING, "Ignoring duplicate item \"{0}\"found in" + " comma-separated value list when converting to set.", item);
        }
        return result;
    }
    
}

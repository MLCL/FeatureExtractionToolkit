/*
 * Copyright (c) 2010-2013, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

import com.beust.jcommander.IStringConverter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hiam20
 */
public final class OutputFormatterConverter implements IStringConverter<OutputFormatter> {

    private static final Logger LOG = Logger.getLogger(OutputFormatterConverter.class.getName());

    @Override
    public final OutputFormatter convert(final String value) {
        OutputFormatter f = null;
        try {
            try {
                f = ((Class<OutputFormatter>) Class.forName(value)).newInstance();
            } catch (ClassNotFoundException e) {
                LOG.log(Level.WARNING, "OutputFormatter {0} not found.", value);
                f = new NewlineOutputFormatter();
            }
        } catch (InstantiationException e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (IllegalAccessException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return f;
    }

}

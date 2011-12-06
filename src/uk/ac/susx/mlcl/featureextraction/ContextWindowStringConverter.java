/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author Simon Wibberley
 */
public final class ContextWindowStringConverter implements IStringConverter<IntSpan> {

    @Override
    public final IntSpan convert(String value) {
        final int pos1 = value.indexOf('-');

        if (pos1 != 0)
            throw new ParameterException("Left context width not given.");

        final int pos2 = value.indexOf('+');

        if (pos2 == -1)
            throw new ParameterException("Right context width not given.");
        if (pos2 - pos1 < 1)
            throw new ParameterException("Left context width is empty.");
        if (value.length() - pos2 < 1)
            throw new ParameterException("Right context width is empty.");

        try {
            final int left = Integer.parseInt(value.substring(pos1, pos2));

            if (left > 0)
                throw new ParameterException("left > 0");

            final int right = Integer.parseInt(value.substring(pos2 + 1));

            if (right < 0)
                throw new ParameterException("right < 0");


            return new IntSpan(left, right);
        } catch (NumberFormatException e) {
            throw new ParameterException(e);
        }

    }

}

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

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hiam20
 */
public final class FormatterConverter implements IStringConverter<OutputFormatter> {

    private static final Logger LOG = Logger.getLogger(FormatterConverter.class.getName());

    @Override
    public final OutputFormatter convert(final String value) {
        OutputFormatter f = null;
        try {
            try {
                f = ((Class<OutputFormatter>) Class.forName(value)).newInstance();
            } catch (ClassNotFoundException e) {
                LOG.log(Level.WARNING, "Formatter: {0} not found.", value);
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

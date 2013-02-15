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

import java.io.Serializable;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Simon Wibberley
 */
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(Config.class.getName());

    @Parameter(names = {"-nc", "--numCores"},
               description = "Number of concurrent worker threads to use.")
    private int numCores = Runtime.getRuntime().availableProcessors() + 1;

    @Parameter(names = {"-h", "--help"},
               description = "Display this help message.")
    private boolean usageRequested = false;

    public boolean isUsageRequested() {
        return usageRequested;
    }

    public void load(String[] args) {

        JCommander jc = new JCommander(this, args);

        try {
            jc.parse();

            if (isUsageRequested()) {
                jc.usage();
                if (jc.getParsedCommand() != null) {
                    jc.usage(jc.getParsedCommand());
                }
                // XXX (Hamish): Pretty sure this is redundant and bad :-(
                System.exit(0);
            }

        } catch (ParameterException e) {
            LOG.log(Level.SEVERE, null, e);
            jc.usage();
        }
    }

    /**
     * @return the numCores
     */
    public int getNumCores() {
        return numCores;
    }

}

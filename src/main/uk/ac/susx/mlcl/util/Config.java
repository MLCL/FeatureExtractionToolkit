/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
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

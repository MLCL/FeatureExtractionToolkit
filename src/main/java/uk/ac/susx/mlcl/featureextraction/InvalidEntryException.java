/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

/**
 * 
 * @author Simon Wibberley
 */
public class InvalidEntryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidEntryException() {
        super();
    }

    public InvalidEntryException(String message) {
        super(message);
    }
}

/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.Collection;

import javax.naming.OperationNotSupportedException;

/**
 * 
 * @author Simon Wibberley
 * @param <T> 
 */
public interface Annotation<T> {

    T getValue();

    void setValue(T v);

    void addToCollection(
            Collection<? super String> list, String prefix)
            throws OperationNotSupportedException;

}
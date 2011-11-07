/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.Collection;
import javax.naming.OperationNotSupportedException;

/**
 *
 * @param <T> 
 * @author Simon Wibberley
 */
public abstract class AbstractAnnotation<T> implements Annotation<T> {

    private T value;

    public AbstractAnnotation(T value) {
        this.value = value;
    }

    public AbstractAnnotation() {
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T v) {
        value = v;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @SuppressWarnings(value = "unchecked")
    public Class<T> getType() {
        return (Class<T>) value.getClass();
    }

    @Override
    public void addToCollection(Collection<? super String> list, String prefix)
            throws OperationNotSupportedException {
        list.add(prefix + value.toString());
    }

}

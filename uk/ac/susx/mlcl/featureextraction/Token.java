/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

/**
 * 
 * @author Simon Wibberley
 */
public class Token {

    public Map<Class<? extends Annotation<?>>, Annotation<?>> annotations;

    public Token() {
        annotations = new HashMap<Class<? extends Annotation<?>>, Annotation<?>>();
    }

    public <T> void set(Class<? extends Annotation<T>> a, T v) {
        set(Annotations.get(a, v));
    }

    @SuppressWarnings("unchecked")
    public void set(Annotation<?> a) {
        annotations.put((Class<? extends Annotation<?>>) a.getClass(), a);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<? extends Annotation<T>> k) {
        Annotation<T> a = (Annotation<T>) annotations.get(k);
        return (a == null) ? null : a.getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String k) {
        try {
            return get((Class<? extends Annotation<T>>) Class.forName(k));
        } catch (ClassNotFoundException e) {
            return null;
        }

    }

    public String getString(String k) {
        try {
            return get(k).toString();
        } catch (NullPointerException e) {
            return null;
        }
//        try {
//            return annotations.get(Class.forName(k)).getValue().toString();
//        } catch (NullPointerException e) {
//            return null;
//        } catch (ClassNotFoundException e) {
//            return null;
//        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        boolean first = true;
        for (Annotation<?> a : annotations.values()) {
            if(!first) {
                sb.append(", ");
                first = true;
            }
            sb.append(a.toString());
        }
        sb.append("] ");

        return sb.toString();
    }

    public <T> void addAnnotationToCollection(Class<? extends Annotation<T>> k,
                                              Collection<? super String> list,
                                              String prefix) {
        try {
            annotations.get(k).addToCollection(list, prefix);
        } catch (OperationNotSupportedException e) {
        }
    }
}
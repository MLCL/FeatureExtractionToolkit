/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;

/**
 * Static utility class for handling Annotation objects.
 * 
 * @author Simon Wibberley
 */
public class Annotations {

    private static final Logger LOG = Logger.getLogger(
            Annotations.class.getName());

    private Annotations() {
        // Static utility class is non-instantiable
    }

    public static <T> Annotation<T> get(Class<? extends Annotation<T>> cls,
                                        T value) {

        Annotation<T> a = null;
        try {
            a = cls.newInstance();
            a.setValue(value);
        } catch (InstantiationException e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (IllegalAccessException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return a;
    }

    public static abstract class AbstractObjectAnnotation<T> implements Annotation<T> {

        private T value;

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

        @SuppressWarnings("unchecked")
        public Class<T> getType() {
            return (Class<T>) value.getClass();
        }

        @Override
        public void addToCollection(
                Collection<? super String> list, String prefix)
                throws OperationNotSupportedException {
            list.add(prefix + value.toString());
        }
    }

    public static abstract class AbstractStringAnnotation extends AbstractObjectAnnotation<String> {
    }

    public abstract static class AbstractIntegerAnnotation extends AbstractObjectAnnotation<Integer> {
    }

    public abstract static class AbstractStringListAnnotation extends AbstractObjectAnnotation<List<String>> {

        @Override
        public String toString() {
            final StringBuilder out = new StringBuilder();
            boolean first = true;
            for (String s : getValue()) {
                if (!first) {
                    out.append("\t");
                }
                out.append(s);
                first = false;
            }
            return out.toString();
        }

        @Override
        public void addToCollection(Collection<? super String> list,
                                    String prefix) {
            for (String c : getValue()) {
                list.add(prefix + c);
            }
        }
    }

    public static abstract class AbstractSpanAnnotation extends AbstractObjectAnnotation<int[]> {

        @Override
        public void addToCollection(Collection<? super String> list,
                                    String prefix) throws OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }

        @Override
        public String toString() {
            return Arrays.toString(getValue());
        }
    }

    public static class CharAnnotation extends AbstractObjectAnnotation<Character> {
    }

    public static class NgramSpanAnnotation extends AbstractObjectAnnotation<List<int[]>> {

        @Override
        public void addToCollection(
                Collection<? super String> list, String prefix)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

    public static class ContextAnnotation extends AbstractStringListAnnotation {
    }

    public static class NgramAnnotation extends AbstractStringListAnnotation {
    }

    public static class ChunkSpanAnnotation extends AbstractSpanAnnotation {
    }

    public static class BaseAnnotation extends AbstractSpanAnnotation {
    }

    public static class IndexAnnotation extends AbstractIntegerAnnotation {
    }

    public static class SentenceLengthAnnotation extends AbstractIntegerAnnotation {
    }

    public static class WordAnnotation extends AbstractStringAnnotation {
    }

    public static class TokenAnnotation extends AbstractStringAnnotation {
    }

    public static class NERAnnotation extends AbstractStringAnnotation {
    }

    public static class PoSAnnotation extends AbstractStringAnnotation {
    }

    public static class ChunkAnnotation extends AbstractStringAnnotation {
    }

    public static class ChunkTagAnnotation extends AbstractStringAnnotation {
    }

    public static class NEAnnotation extends AbstractStringAnnotation {
    }

    public static class AnswerAnnotation extends AbstractStringAnnotation {
    }

    public static class __FeatureAnnotation extends AbstractStringListAnnotation {
    }

    public static class __KeyAnnotation extends AbstractStringAnnotation {
    }

    public static class __SpanAnnotation extends AbstractSpanAnnotation {
    }
}

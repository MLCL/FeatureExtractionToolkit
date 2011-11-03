/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

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

    public static <T> Annotation<T> createAnnotation(Class<? extends Annotation<T>> cls,
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

    public static class CharAnnotation extends AbstractAnnotation<Character> {
    }

    public static class NgramSpanAnnotation extends AbstractAnnotation<List<int[]>> {

        @Override
        public void addToCollection(
                Collection<? super String> list, String prefix)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }
//
//    public static class ContextAnnotation extends AbstractListAnnotation<String> {
//    }

    public static class NgramAnnotation extends AbstractListAnnotation<CharSequence> {
    }

    public static class ChunkSpanAnnotation extends AbstractSpanAnnotation {
    }
//
//    public static class BaseAnnotation extends AbstractSpanAnnotation {
//    }

    public static class IndexAnnotation extends AbstractAnnotation<Integer> {
    }

    public static class SentenceLengthAnnotation extends AbstractAnnotation<Integer> {
    }

//    public static class WordAnnotation extends AbstractAnnotation<String> {
//    }

    public static class TokenAnnotation extends AbstractAnnotation<CharSequence> {
    }

//    public static class NERAnnotation extends AbstractAnnotation<String> {
//    }

    public static class PoSAnnotation extends AbstractAnnotation<String> {
    }

    public static class ChunkAnnotation extends AbstractAnnotation<CharSequence> {
    }

    public static class ChunkTagAnnotation extends AbstractAnnotation<String> {
    }

//    public static class NEAnnotation extends AbstractAnnotation<String> {
//    }

//    public static class AnswerAnnotation extends AbstractAnnotation<String> {
//    }

  
   

  
}

/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;
import uk.ac.susx.mlcl.util.IntSpan;
import uk.ac.susx.mlcl.util.Tuple;

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

    public static class NgramSpanAnnotation extends AbstractAnnotation<List<IntSpan>> {

        @Override
        public void addToCollection(
                Collection<? super String> list, String prefix)
                throws OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }

    }
    
    public static class DependencyHeadListAnnotation extends AbstractDictionaryAnnotation<HashMap<String,ArrayList<String>>> {
    }
    
    public static class DependencyListAnnotation extends AbstractDictionaryAnnotation<HashMap<String,ArrayList<String>>> {   
    }
    
    public static class DependencyAnnotation extends AbstractAnnotation<String>{
    }

    public static class NgramAnnotation extends AbstractListAnnotation<CharSequence> {
    }

    public static class ChunkSpanAnnotation extends AbstractSpanAnnotation {
    }

    public static class IndexAnnotation extends AbstractAnnotation<Integer> {
    }

    public static class SentenceLengthAnnotation extends AbstractAnnotation<Integer> {
    }

    public static class TokenAnnotation extends AbstractAnnotation<CharSequence> {
    }
    
    public static class OriginalTokenAnnotation extends AbstractAnnotation<CharSequence> {
    }

    public static class PoSAnnotation extends AbstractAnnotation<String> {
    }

    public static class ChunkAnnotation extends AbstractAnnotation<CharSequence> {
    }

    public static class ChunkTagAnnotation extends AbstractAnnotation<String> {
    }
    
    // NEW ADDITIONS *************************************************************
    
    public static class LeftNounAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class RightNounAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class LeftAdjectiveAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class RightAdjectiveAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class LeftVerbHeadAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class RightVerbHeadAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class LeftPrepositionAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class RightPrepositionAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class HeadNounAnnotation extends AbstractAnnotation<String> {
    }
    
    public static class OntologyEntryAnnotation extends AbstractAnnotation<String>{
    }
    
    public static class PoSTagLeftAnnotation extends AbstractAnnotation<String>{
    }
    
    public static class PoSTagRightAnnotation extends AbstractAnnotation<String>{
    }   
    
    public static class DeterminerLeftAnnotation extends AbstractAnnotation<String>{
    }
    
    public static class NounGroupNounAnnotation extends AbstractAnnotation<String>{
    }
    //****************************************************************************
}

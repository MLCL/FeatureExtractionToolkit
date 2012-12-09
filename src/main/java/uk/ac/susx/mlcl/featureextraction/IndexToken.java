/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import uk.ac.susx.mlcl.util.IntSpan;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import java.util.Comparator;
import java.util.List;

import uk.ac.susx.mlcl.featureextraction.annotations.AbstractAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.AbstractListAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.AbstractSpanAnnotation;

/**
 * 
 * @param <T> 
 * @author Simon Wibberley
 */
public class IndexToken<T> extends Token implements Comparable<IndexToken<?>> {

    private final Class<? extends Annotation<T>> keyType;

    public IndexToken(IntSpan span, Class<? extends Annotation<T>> type) {
        super();
        setAnnotation(__SpanAnnotation.class, span);
        keyType = type;
    }

    public Class<? extends Annotation<T>> getKeyType() {
        return keyType;
    }

    public IntSpan getSpan() {
        return getAnnotation(__SpanAnnotation.class);
    }

    public CharSequence getKey() {
        return getAnnotation(__KeyAnnotation.class);
    }

    public void setKey(CharSequence key) {
        setAnnotation(__KeyAnnotation.class, key);
    }

    public List<CharSequence> getFeatures() {
        return getAnnotation(__FeatureAnnotation.class);
    }

    public void setFeatures(List<CharSequence> feature) {
        setAnnotation(__FeatureAnnotation.class, feature);
    }

    @Override
    public int compareTo(IndexToken<?> other) {
        return BEGIN_ORDER.compare(this, other);
    }

    public boolean equals(IndexToken<?> other) {
        return compareTo(other) == 0;
    }

    public static final Comparator<IndexToken<?>> BEGIN_ORDER =
            new Comparator<IndexToken<?>>() {

                @Override
                public int compare(IndexToken<?> arg0, IndexToken<?> arg1) {
                    return IntSpan.LEFT_FIRST_COMPARATOR.compare(arg0.getSpan(), arg1.getSpan());
                }

            };

    public static final Comparator<IndexToken<?>> END_ORDER =
            new Comparator<IndexToken<?>>() {

                @Override
                public int compare(IndexToken<?> arg0, IndexToken<?> arg1) {
                    return IntSpan.RIGHT_FIRST_COMPARATOR.compare(arg0.getSpan(), arg1.getSpan());
                }

            };

    public static class __SpanAnnotation extends AbstractSpanAnnotation {

        public __SpanAnnotation() {
            // The public constructor is required, otherwise the class 
            // can't be instantiated through relfection.
        }

    }

    public static class __FeatureAnnotation extends AbstractListAnnotation<CharSequence> {

        public __FeatureAnnotation() {
            // The public constructor is required, otherwise the class 
            // can't be instantiated through relfection.
        }

    }

    public static class __KeyAnnotation extends AbstractAnnotation<CharSequence> {

        public __KeyAnnotation() {
            // The public constructor is required, otherwise the class 
            // can't be instantiated through relfection.
        }

    }
}
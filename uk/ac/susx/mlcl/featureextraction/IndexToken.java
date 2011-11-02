/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.Comparator;
import java.util.List;

import uk.ac.susx.mlcl.featureextraction.Annotations.__FeatureAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__KeyAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__SpanAnnotation;

/**
 * 
 * @author Simon Wibberley
 */
public class IndexToken<T> extends Token implements Comparable<IndexToken<?>> {

    private Class<? extends Annotation<T>> keyType;

    public IndexToken(int[] span, Class<? extends Annotation<T>> type) {
        super();
        set(__SpanAnnotation.class, span);
        keyType = type;
    }

    public Class<? extends Annotation<T>> getKeyType() {
        return keyType;
    }

    public int[] getSpan() {
        return get(__SpanAnnotation.class);
    }

    public CharSequence getKey() {
        return get(__KeyAnnotation.class);
    }

    public void setKey(CharSequence key) {
        set(__KeyAnnotation.class, key);
    }

    public List<CharSequence> getFeature() {
        return get(__FeatureAnnotation.class);
    }

    public void setFeature(List<CharSequence> feature) {
        set(__FeatureAnnotation.class, feature);
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
                    int[] span0 = arg0.getSpan();
                    int[] span1 = arg1.getSpan();
                    if (span0[0] < span1[0]) {
                        return -1;
                    } else if (span0[0] == span1[0]) {
                        if (span0[1] < span1[1]) {
                            return -1;
                        } else if (span0[1] == span1[1]) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else {
                        return 1;
                    }
                }
            };

    public static final Comparator<IndexToken<?>> END_ORDER =
            new Comparator<IndexToken<?>>() {

                @Override
                public int compare(IndexToken<?> arg0, IndexToken<?> arg1) {
                    int[] span0 = arg0.getSpan();
                    int[] span1 = arg1.getSpan();
                    if (span0[1] < span1[1]) {
                        return -1;
                    } else if (span0[1] == span1[1]) {
                        if (span0[0] < span1[0]) {
                            return -1;
                        } else if (span0[0] == span1[0]) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else {
                        return 1;
                    }
                }
            };

}

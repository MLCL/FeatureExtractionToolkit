/*
 * Copyright (c) 2010-2013, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

    // XXX (Hamish): This is pretty bad code because equals and compare can produce different results (when compared
    //               against objects of a different type. Instead equals and hashcode should be implemented normally
    //               for this class as Token super class, then the ordering can be applied with a Comparator.
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

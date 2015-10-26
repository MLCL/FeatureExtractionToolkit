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
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.CharAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.NgramAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.NgramSpanAnnotation;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.FeatureConstraint;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * 
 * @author Simon Wibberley
 */
public class NGramFeatureFunction extends AbstractFeatureFunction {

    private IntSpan window;

    private int nmin;

    private int nmax;

    public NGramFeatureFunction(IntSpan w, int min, int max) {
        window = w;
        nmin = min;
        nmax = max;
    }

    public int getNmax() {
        return nmax;
    }

    public int getNmin() {
        return nmin;
    }

    public IntSpan getWindow() {
        return window;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {

        Collection<String> features = new ArrayList<String>();
        getLeftNgrams(sentence, index);
        getRightNgrams(sentence, index);
        for (int i = 0; i < sentence.size(); ++i) {
            Token t = sentence.get(i);
            boolean accept = true;
            for (FeatureConstraint c : getConstraints()) {
                if (!c.accept(sentence, index, i)) {
                    accept = false;
                    break;
                }
            }
            if (accept) {
                t.addAnnotationToCollection(NgramAnnotation.class, features, getPrefix());
            }
        }
        return features;
    }

    private void getLeftNgrams(Sentence s, IndexToken<?> index) {
        if (Math.abs(window.left) < 1)
            return;

        IntSpan indexSpan = index.getSpan();
        int begin = Math.max(0, indexSpan.left + window.left);
        int end = indexSpan.left;

        for (int i = begin; i < end; ++i) {
            s.get(i).removeAnnotation(NgramSpanAnnotation.class);;
            s.get(i).removeAnnotation(NgramAnnotation.class);;

            final List<IntSpan> spans = new ArrayList<IntSpan>();
            final List<CharSequence> ngrams = new ArrayList<CharSequence>();
            final StringBuilder sb = new StringBuilder();

            for (int n = nmin; n <= nmax; n++) {
                int j = i - (n - 1);

                if (j < 0)
                    break;

                final char c = s.get(j).getAnnotation(CharAnnotation.class);
                sb.insert(0, c);
                IntSpan span = new IntSpan(j, i);
                spans.add(span);
                ngrams.add(sb.toString());
            }

            s.get(i).setAnnotation(NgramSpanAnnotation.class, spans);
            s.get(i).setAnnotation(NgramAnnotation.class, ngrams);

        }
    }

    private void getRightNgrams(Sentence s, IndexToken<?> index) {
        if (Math.abs(window.right) < 1)
            return;
        IntSpan indexSpan = index.getSpan();
        int begin = indexSpan.right + 1;
        int end = Math.min(s.size(), indexSpan.right + window.right + 1);

        for (int i = begin; i < end; ++i) {

            s.get(i).removeAnnotation(NgramSpanAnnotation.class);;
            s.get(i).removeAnnotation(NgramAnnotation.class);;

            final List<IntSpan> spans = new ArrayList<IntSpan>();
            final List<CharSequence> ngrams = new ArrayList<CharSequence>();
            final StringBuilder sb = new StringBuilder();

            for (int n = nmin; n <= nmax; n++) {
                int j = i + n - 1;
                if (j >= s.size())
                    break;

                final char c = s.get(j).getAnnotation(CharAnnotation.class);
                sb.append(c);
                IntSpan span = new IntSpan(i, j);
                spans.add(span);
                ngrams.add(sb.toString());
            }

            s.get(i).setAnnotation(NgramSpanAnnotation.class, spans);
            s.get(i).setAnnotation(NgramAnnotation.class, ngrams);

        }

    }

}

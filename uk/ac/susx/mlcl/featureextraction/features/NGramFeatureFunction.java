/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.CharAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.NgramAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.NgramSpanAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;

/**
 *
 * @author hiam20
 */
public class NGramFeatureFunction extends AbstractFeatureFunction {

    private int[] window;

    private int nmin;

    private int nmax;
    //private Sentence cache;
    //private Sentence ngrams;

    public NGramFeatureFunction(int[] w, int min, int max) {
        window = w;
        nmin = min;
        nmax = max;
        addConstraint(new FeatureConstraint() {

            @Override
            public boolean accept(Sentence s, IndexToken<?> cur, int i) {
                return s.get(i).getAnnotation(NgramAnnotation.class) != null;
            }
        });
        addConstraint(new FeatureConstraint() {

            @Override
            public boolean accept(Sentence s, IndexToken<?> cur, int i) {
                int[] curSpan = cur.getSpan();
                return i < curSpan[0] || i >= curSpan[1];
            }
        });
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
        int[] indexSpan = index.getSpan();
        int begin = Math.max(0, indexSpan[0] + window[0]);
        int end = indexSpan[0];
        for (int i = begin; i < end; ++i) {
            List<int[]> spans = new ArrayList<int[]>();
            List<CharSequence> ngrams = new ArrayList<CharSequence>();
            StringBuilder sb = new StringBuilder();
            for (int j = i + nmin - 1; j < Math.min(i + nmax, end); ++j) {
                char c = s.get(j).getAnnotation(CharAnnotation.class);
                sb.append(c);
                //System.err.println(sb.toString());
                int[] span = new int[]{i, j};
                spans.add(span);
                ngrams.add(sb);
            }
            s.get(i).setAnnotation(NgramSpanAnnotation.class, spans);
            
            s.get(i).setAnnotation(NgramAnnotation.class, ngrams);
            
        }
    }

    private void getRightNgrams(Sentence s, IndexToken<?> index) {
        int[] indexSpan = index.getSpan();
        int begin = indexSpan[1];
        int end = Math.min(s.size(), indexSpan[1] + window[1] + 1);
        for (int i = begin; i < end; ++i) {
            List<int[]> spans = new ArrayList<int[]>();
            List<CharSequence> ngrams = new ArrayList<CharSequence>();
            StringBuilder sb = new StringBuilder();
            for (int j = i + nmin - 1; j < Math.min(i + nmax, end); ++j) {
                char c = s.get(j).getAnnotation(CharAnnotation.class);
                sb.append(c);
                //System.err.println(sb.toString());
                int[] span = new int[]{i, j};
                spans.add(span);
                ngrams.add(sb);
            }
            s.get(i).setAnnotation(NgramSpanAnnotation.class, spans);
            s.get(i).setAnnotation(NgramAnnotation.class, ngrams);
        }
    }
    
}

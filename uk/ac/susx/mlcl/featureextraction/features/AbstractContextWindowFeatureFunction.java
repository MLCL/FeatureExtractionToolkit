/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;

/**
 *
 * @author hiam20
 */
public abstract class AbstractContextWindowFeatureFunction extends AbstractFeatureFunction {

    
    public AbstractContextWindowFeatureFunction(final int[] window) {
        addConstraint(new FeatureConstraint() {

            @Override
            public boolean accept(Sentence s, IndexToken<?> cur, int i) {
                return isInInclusiveWindow(s, cur, i, window);
            }
        });
    }
//
//    protected <T> void buildContext(List<String> context, List<Token> sentence, Class<? extends Annotation<T>> annonation) {
//        //String prefix = getPrefix();
//        for (Token t : sentence) {
//            context.add("" + t.getAnnotation(annonation));
//        }
//    }

    protected <T> Collection<String> extractFeatures(
            Sentence sentence, IndexToken<?> index, Class<? extends Annotation<T>> annotation) {
        List<String> context = new ArrayList<String>();
        for (int i = 0; i < sentence.size(); ++i) {
            // for(int i = from; i < to; ++i) {
            // for(Token t : sentence) {
            // int i = t.get(IndexAnnotation.class);
            
            Token t = sentence.get(i);
            boolean accept = true;
            for (FeatureConstraint constraint : getConstraints()) {
                if (!constraint.accept(sentence, index, i)) {
                    accept = false;
                    break;
                }
            }
            if (accept) {
                t.addAnnotationToCollection(annotation, context, getPrefix());
                //context.add(prefix + t.get(annotation));
            }
        }
        return context;
    }

    protected static boolean isInInclusiveWindow(int windowLeft, int windowRight, int tokenLeft, int tokenRight, int i, int len) {
        int from = Math.max(tokenLeft - Math.abs(windowLeft), 0);
        int to = Math.min(windowRight + tokenRight, len);
        return i >= from && i < to;
    }

    protected static boolean isInInclusiveWindow(Sentence s, IndexToken<?> cur, int i, int[] w) {
        int[] span = cur.getSpan();
        return isInInclusiveWindow(w[0], w[1], span[0], span[1], i, s.size());
    }

    protected static boolean isInExclusiveWindow(int tokenLeft, int tokenRight, int indexLeft, int indexRight, int i) {
        return (tokenRight <= indexLeft && i == tokenRight - 1) || (indexRight <= tokenLeft && i == tokenLeft);
    }

    protected static boolean isInExclusiveWindow(Sentence s, IndexToken<?> index, int i) {
        Token token = s.get(i);
        int[] tSpan = token.getAnnotation(ChunkSpanAnnotation.class);
        int[] iSpan = index.getSpan();
        return isInExclusiveWindow(tSpan[0], tSpan[1], iSpan[0], iSpan[1], i);
    }
    
}

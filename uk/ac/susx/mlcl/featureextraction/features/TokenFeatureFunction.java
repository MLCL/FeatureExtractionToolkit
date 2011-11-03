/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.TokenAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author hiam20
 */
public class TokenFeatureFunction extends AbstractContextWindowFeatureFunction {

    public TokenFeatureFunction(int[] config) {
                super(config);

        addConstraint(new FeatureConstraint() {

            @Override
            public boolean accept(Sentence s, IndexToken<?> cur, int i) {
                int[] span = cur.getSpan();
                //return span[0] == span[1] - 1 && span[0] != i ;
                //return TokenSpan.isInExclusiveWindow(s, cur, i);
                return isInExclusiveWindow(i, i + 1, span[0], span[1], i);
            }
        });
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        return extractFeatures(sentence, index, TokenAnnotation.class);
    }
    
}

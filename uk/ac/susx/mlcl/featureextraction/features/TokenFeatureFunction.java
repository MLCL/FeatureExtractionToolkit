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
public class TokenFeatureFunction extends AbstractFeatureFunction {

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> currentToken) {
        return extractFeatures(sentence, currentToken, TokenAnnotation.class);
    }

}

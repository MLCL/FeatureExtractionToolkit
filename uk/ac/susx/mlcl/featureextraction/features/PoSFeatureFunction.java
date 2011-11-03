/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.PoSAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author hiam20
 */
public class PoSFeatureFunction extends AbstractContextWindowFeatureFunction {

    public PoSFeatureFunction(int[] config) {
        super(config);
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        return extractFeatures(sentence, index, PoSAnnotation.class);
    }
    
}

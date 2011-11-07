/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;

/**
 *
 * @author hiam20
 */
public class MinChunkLengthFeatureConstraint implements FeatureConstraint {

    private final int minLength;

    public MinChunkLengthFeatureConstraint(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean accept(Sentence s, IndexToken<?> index, int i) {
        return s.get(i).getAnnotation(ChunkSpanAnnotation.class).length() >= minLength;
    }
    
}

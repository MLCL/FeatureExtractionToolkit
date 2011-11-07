/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author hiam20
 */
public class MysteriousFeatureConstraint implements FeatureConstraint {

    public MysteriousFeatureConstraint() {
    }

    @Override
    public boolean accept(Sentence sentence, IndexToken<?> currentToken, int index) {
        final IntSpan chunkSpan = sentence.get(index).getAnnotation(ChunkSpanAnnotation.class);
        final IntSpan tokenSpan = currentToken.getSpan();
        return (chunkSpan.right < tokenSpan.left && index == chunkSpan.right) || (tokenSpan.right < chunkSpan.left && index == chunkSpan.left);
    }
    
}

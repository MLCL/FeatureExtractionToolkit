/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author hiam20
 */
public class ChunkFeature extends AbstractContextWindowFeatureFunction {

    public ChunkFeature(int[] config, boolean incSingleChunk) {
        super(config);

        if (!incSingleChunk) {
            addConstraint(new FeatureConstraint() {

                @Override
                public boolean accept(Sentence s, IndexToken<?> index, int i) {
                    int[] span = s.get(i).getAnnotation(ChunkSpanAnnotation.class);
                    return span[0] != span[1] - 1;
                }

            });
        }
        addConstraint(new FeatureConstraint() {

            @Override
            public boolean accept(Sentence s, IndexToken<?> index, int i) {
                return isInExclusiveWindow(s, index, i);
            }

        });
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        return extractFeatures(sentence, index, ChunkAnnotation.class);
    }

}

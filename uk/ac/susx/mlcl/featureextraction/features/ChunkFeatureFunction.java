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
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author hiam20
 */
public class ChunkFeatureFunction extends AbstractFeatureFunction {

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        return extractFeatures(sentence, index, ChunkAnnotation.class);
    }
}

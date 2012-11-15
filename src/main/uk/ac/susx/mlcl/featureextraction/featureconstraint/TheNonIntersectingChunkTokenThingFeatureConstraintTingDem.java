/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * 
 * 
 * @author Simon Wibberley
 */
public class TheNonIntersectingChunkTokenThingFeatureConstraintTingDem
        implements FeatureConstraint {

    @Override
    public boolean accept(Sentence sentence, IndexToken<?> currentToken, int index) {
        final IntSpan chunkSpan = sentence.get(index).getAnnotation(ChunkSpanAnnotation.class);
        final IntSpan tokenSpan = currentToken.getSpan();
        return (chunkSpan.right < tokenSpan.left && index == chunkSpan.right)
                || (tokenSpan.right < chunkSpan.left && index == chunkSpan.left);
    }

}

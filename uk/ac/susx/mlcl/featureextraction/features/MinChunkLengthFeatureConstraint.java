/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;

/**
 *
 * @author Simon Wibberley
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

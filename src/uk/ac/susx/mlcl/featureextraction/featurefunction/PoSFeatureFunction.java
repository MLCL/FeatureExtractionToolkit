/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.PoSAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author Simon Wibberley
 */
public class PoSFeatureFunction extends AbstractFeatureFunction {

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        return extractFeatures(sentence, index, PoSAnnotation.class);
    }

}

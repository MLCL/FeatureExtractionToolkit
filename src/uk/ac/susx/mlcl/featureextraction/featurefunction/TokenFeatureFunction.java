/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.TokenAnnotation;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 * Extends the AbstractFeatureFunction to provide Token Feature specific functionality
 * to the overridden method extractFeatures()
 * 
 * @author Simon Wibberley
 */
public class TokenFeatureFunction extends AbstractFeatureFunction {

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> currentToken) {
        return extractFeatures(sentence, currentToken, TokenAnnotation.class);
    }

}

/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author Simon Wibberley
 */
public interface FeatureFunction {

    Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index);
    
}

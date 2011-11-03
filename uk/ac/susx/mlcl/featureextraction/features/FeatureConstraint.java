/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 * 
 * @author Simon Wibberley
 */
public interface FeatureConstraint {

    boolean accept(Sentence s, IndexToken<?> cur, int i);
}

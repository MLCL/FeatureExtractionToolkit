/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 * An interface to allow a constraint accept method to be instantiated.
 * Returning true if the constraint criteria are met.
 * @author Simon Wibberley
 */
public interface FeatureConstraint {

    boolean accept(Sentence s, IndexToken<?> cur, int i);
}

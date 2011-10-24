/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

/**
 * 
 * @author Simon Wibberley
 */
public interface FeatureConstraint {

    boolean accept(Sentence s, IndexToken<?> cur, int i);
}

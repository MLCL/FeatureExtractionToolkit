/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Simon Wibberley
 */
public abstract class FeatureFunction {

    private String prefix = "";

    private Collection<FeatureConstraint> constraints = new ArrayList<FeatureConstraint>();

    public abstract Collection<String> extractFeatures(
            Sentence sentence, IndexToken<?> index);

    public void init(String config) {
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String p) {
        prefix = p;
    }

    public void addConstraint(FeatureConstraint fc) {
        constraints.add(fc);
    }

    protected Collection<FeatureConstraint> getConstraints() {
        return constraints;
    }
}
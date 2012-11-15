/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.featurefactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import uk.ac.susx.mlcl.featureextraction.featurefunction.FeatureFunction;

/**
 * 
 * Contains a collection of FeatureFunctions with the necessary get and set methods
 * 
 * @author Simon Wibberley
 */
public class FeatureFactory {

    private final Map<String, FeatureFunction> fns;

    public FeatureFactory() {
        fns = new HashMap<String, FeatureFunction>();
    }

    public void addFeature(String key, FeatureFunction fn) {
        fns.put(key, fn);
    }

    public FeatureFunction getFeature(String key) {
        return fns.get(key);
    }

    public Collection<FeatureFunction> getAllFeatures() {
        return fns.values();
    }

}

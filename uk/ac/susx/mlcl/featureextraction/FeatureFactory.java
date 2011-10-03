package uk.ac.susx.mlcl.featureextraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatureFactory {

	public Map<String, FeatureFunction> fns;
	
	public FeatureFactory() {
		fns = new HashMap<String, FeatureFunction>();
	}
	
	public void addFeature(String key, FeatureFunction fn) {
		fns.put(key, fn);
	}
	
	public void addFeature(String key, FeatureFunction fn, String prefix, FeatureConstraint ... constraints) {
		fn.setPrefix(prefix);
		for(FeatureConstraint fc : constraints) {
			fn.addConstraint(fc);
		}
		fns.put(key, fn);
	}
	
	public FeatureFunction getFeature(String key) {
		return fns.get(key);
	}
		
	public Collection<FeatureFunction> getAllFeatures() {
		return fns.values();
	}
}

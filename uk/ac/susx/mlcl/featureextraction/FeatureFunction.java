package uk.ac.susx.mlcl.featureextraction;

import java.util.ArrayList;
import java.util.Collection;

public abstract class FeatureFunction {
	
	protected String prefix = "";
	
	protected Collection<FeatureConstraint> constraints = new ArrayList<FeatureConstraint>();

	public abstract Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index);

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
}
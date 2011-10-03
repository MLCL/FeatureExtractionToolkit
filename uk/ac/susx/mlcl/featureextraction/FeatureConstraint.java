package uk.ac.susx.mlcl.featureextraction;

public interface FeatureConstraint {
	public boolean accept(Sentence s, IndexToken<?> cur, int i);
	//public <T> boolean accept(Sentence s, IndexToken<T> cur, int i);
}

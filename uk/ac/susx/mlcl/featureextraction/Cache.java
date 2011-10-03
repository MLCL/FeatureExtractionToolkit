package uk.ac.susx.mlcl.featureextraction;

public interface Cache {
	<T> T getCache(Object key);
}

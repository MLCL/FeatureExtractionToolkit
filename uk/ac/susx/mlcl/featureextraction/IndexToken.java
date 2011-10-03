package uk.ac.susx.mlcl.featureextraction;

import java.util.List;

import uk.ac.susx.mlcl.featureextraction.Annotations.BaseAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__FeatureAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__KeyAnnotation;

public class IndexToken<T> extends Token {
	
	private Class<? extends Annotation<T>> keyType;
	
	public IndexToken(int[] span, Class<? extends Annotation<T>> type) {
		super();
		set(BaseAnnotation.class, span);
		keyType = type;
	}
	
	public Class<? extends Annotation<T>> getKeyType() {
		return keyType;
	}
	public int[] getSpan() {
		return get(BaseAnnotation.class);
	}
	public String getKey() {
		return get(__KeyAnnotation.class);
	}
	public void setKey(String key) {
		set(__KeyAnnotation.class, key);
	}
	public List<String> getFeature() {
		return get(__FeatureAnnotation.class);
	}
	public void setFeature(List<String> feature) {
		set(__FeatureAnnotation.class, feature);
	}
}

package uk.ac.susx.mlcl.featureextraction;

import java.util.Comparator;
import java.util.List;

import uk.ac.susx.mlcl.featureextraction.Annotations.BaseAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__FeatureAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__KeyAnnotation;
import uk.ac.susx.mlcl.featureextraction.Annotations.__SpanAnnotation;

public class IndexToken<T> extends Token implements Comparable<IndexToken<?>> {
	
	private Class<? extends Annotation<T>> keyType;
	
	public IndexToken(int[] span, Class<? extends Annotation<T>> type) {
		super();
		set(__SpanAnnotation.class, span);
		keyType = type;
	}
	
	public Class<? extends Annotation<T>> getKeyType() {
		return keyType;
	}
	public int[] getSpan() {
		return get(__SpanAnnotation.class);
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

	@Override
	public int compareTo(IndexToken<?> other) {
		int[] otherSpan = other.getSpan();
		int[] span = getSpan();
		if(span[0] < otherSpan[0]) {
			return -1;
		} else if(span[0] == otherSpan[0]) {
			if(span[1] < otherSpan[1]) {
				return -1;
			} else if (span[1] == otherSpan[1]) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
	
	public boolean equals(IndexToken<?> other) {
		
		return compareTo(other) == 0;
	}
	public static class BeginOrder implements Comparator<IndexToken<?>> {

		@Override
		public int compare(IndexToken<?> arg0, IndexToken<?> arg1) {
			int[] span0 = arg0.getSpan();
			int[] span1 = arg1.getSpan();
			if(span0[0] < span1[0]) {
				return -1;
			} else if(span0[0] == span1[0]) {
				if(span0[1] < span1[1]) {
					return -1;
				} else if (span0[1] == span1[1]) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		}
	}
	
	public static class EndOrder implements Comparator<IndexToken<?>> {

		@Override
		public int compare(IndexToken<?> arg0, IndexToken<?> arg1) {
			int[] span0 = arg0.getSpan();
			int[] span1 = arg1.getSpan();
			if(span0[1] < span1[1]) {
				return -1;
			} else if(span0[1] == span1[1]) {
				if(span0[0] < span1[0]) {
					return -1;
				} else if (span0[0] == span1[0]) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		}
		
	}
}

package uk.ac.susx.mlcl.featureextraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Sentence extends ArrayList<Token> {

	private static final long serialVersionUID = 1L;
	private Collection<IndexToken<?>> keys;
	private String tokenSeparator = "+";
	private String featureSeparator = "\t";
	
	public Sentence(String ws) {
		this();
		tokenSeparator = ws;
	}
	
	public Sentence() {
		super();
		keys = new ArrayList<IndexToken<?>>();
	}	
	
	public Collection<IndexToken<?>> getKeys() {
		return keys;
	}
	
	public List<Token> get() {
		return (List<Token>)this;
	}
	
	public void addKey(IndexToken<?> key) {
		keys.add(key);
	}
	
	public void addAllKey(Collection<? extends IndexToken<?>> ks) {
		keys.addAll(ks);
	}
	
	public String getKeyString(IndexToken<?> key) {
		
		int[] span = key.getSpan();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = span[0]; i < span[1]; ++i ) {
			sb.append(get(i).get(key.getKeyType()));
			if (i < span[1]-1) {
				sb.append(tokenSeparator);
			}
		}
		
		return sb.toString();
		
	}
	
	public void applyFeatureFactory(FeatureFactory featureFactory) {
		Collection<FeatureFunction> fns = featureFactory.getAllFeatures();

		for(IndexToken<?> key : keys) {
			String keyStr = getKeyString(key);
			//StringBuilder featStr = new StringBuilder();
			List<String> featureList = new ArrayList<String>();
			key.setKey(keyStr);
			//System.err.println(keyStr);
			
			for (FeatureFunction f : fns) {
				
				Collection<String> features = f.extractFeatures(this, key);
				featureList.addAll(features);
				/*
				for (String s : features) {
				
					
					featStr.append(s);
					featStr.append(featureSeparator);
				}
				*/
				
			}
			
			key.setFeature(featureList);
			//System.err.print(featStr.toString());
			//System.err.println("\n---------------------------");
		}
		
	}
	
}

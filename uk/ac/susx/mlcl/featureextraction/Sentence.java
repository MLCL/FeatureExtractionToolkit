package uk.ac.susx.mlcl.featureextraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import uk.ac.susx.mlcl.featureextraction.Annotations.TokenAnnotation;

public class Sentence extends ArrayList<Token> {

	private static final long serialVersionUID = 1L;
	private Collection<IndexToken<?>> keys;
	private String tokenSeparator = "+"; 
	private boolean sortedKeys;
	
	public Sentence() {
		this("+");
	}
	
	public Sentence(String ts) {
		this(ts, false);
	}
	
	public Sentence(boolean sk) {
		this("+", sk);
	}
	
	public Sentence(String ts, boolean sk) {
		super();
		tokenSeparator = ts;
		sortedKeys = sk;
		if(sk) {
			keys = new TreeSet<IndexToken<?>>(new IndexToken.EndOrder());
		} else {
			keys = new ArrayList<IndexToken<?>>();
		}
	}	
	
	public boolean isSortedKeys() {
		return sortedKeys;
	}
	
	public String getTokenSeparator() {
		return tokenSeparator;
	}
	public void setTokenSeparator(String ts) {
		tokenSeparator = ts;
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
				
					System.err.println(s);
					//featStr.append(s);
					//featStr.append(featureSeparator);
				}
				*/
				
			}
			
			key.setFeature(featureList);
			//System.err.print(featStr.toString());
			//System.err.println("\n---------------------------");
		}
		
	}
	
	public void useShortestSegmentationPathKeys() {
		
		
		
	}
	
	public Collection<IndexToken<?>> getShortestSegmentationPathKeys() {
		
		IndexToken<?>[] path = new IndexToken<?>[size()];
		int[] shortest = new int[size()];
		Arrays.fill(path, null);
		Arrays.fill(shortest, Integer.MAX_VALUE);
		
		Iterator<IndexToken<?>> itr = keys.iterator();
		
		if(!itr.hasNext()) {
			return null;
		}
		//IndexToken<?> key = itr.next();
		
		
		for(IndexToken<?> key : keys) {
			
			int[] span = key.getSpan();
			
			int begin = span[0];
			int end = span[1]-1;
//			int len = end-begin;
			
			if(shortest[begin] + 1 < shortest[end]) {
				shortest[end] = shortest[begin] + 1; 
				path[end] = key;
			}
		}
		
		
		
		/*
		for(IndexToken<?> key : path) {
			System.err.println(key);
		}
		*/
		
		Collection<IndexToken<?>> shortestPath = getPath(path);
		
		/*
		for(IndexToken<?> key : shortestPath) {
			System.err.println(key);
		}
		*/
		
		return shortestPath;
		
	}
	private Collection<IndexToken<?>> getPath(IndexToken<?>[] keys) {
		
		Collection<IndexToken<?>> path = new ArrayList<IndexToken<?>>();
		getPath(keys, keys.length-1, path);
		return path;
		
	}
	
	private void getPath(IndexToken<?>[] keys, int i, Collection<IndexToken<?>> path ) {
		if(i < 0) {
			return;
		}
		
		IndexToken<?> key = keys[i];
		if(key == null) {
			getPath(keys, i-1, path);
			return;
		}
		
		path.add(key);
		int begin = key.getSpan()[0]-1;
		//System.err.println(begin + " : " + i);
		getPath(keys, begin, path);
		
	}
	
	
	public static void main(String[] args) {
		
		Sentence s = new Sentence(true);
		s.addKey(new IndexToken<String>(new int[]{0,1}, TokenAnnotation.class));
		//s.addKey(new IndexToken<String>(new int[]{1,2}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{2,3}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{3,4}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{4,5}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{5,6}, TokenAnnotation.class));
		
		//s.addKey(new IndexToken<String>(new int[]{2,4}, TokenAnnotation.class));
		//s.addKey(new IndexToken<String>(new int[]{1,4}, TokenAnnotation.class));
		/*
		s.addKey(new IndexToken<String>(new int[]{0,1}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{1,2}, TokenAnnotation.class));
		
		s.addKey(new IndexToken<String>(new int[]{4,6}, TokenAnnotation.class));
		
		
		s.addKey(new IndexToken<String>(new int[]{0,4}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{4,5}, TokenAnnotation.class));
		s.addKey(new IndexToken<String>(new int[]{5,6}, TokenAnnotation.class));
		*/
		s.addKey(new IndexToken<String>(new int[]{4,6}, TokenAnnotation.class));
		
		s.add(new Token());
		s.add(new Token());
		s.add(new Token());
		s.add(new Token());
		s.add(new Token());
		s.add(new Token());
		
		Collection<IndexToken<?>> path = s.getShortestSegmentationPathKeys();
		
		
		for(IndexToken<?> key : path) {
			System.err.println(key);
		}
		
		
		
	}
}



















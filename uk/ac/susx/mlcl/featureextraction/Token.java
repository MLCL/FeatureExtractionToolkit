package uk.ac.susx.mlcl.featureextraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;



public class Token {
	
	public Map<Class<Annotation<?>>, Annotation<?>> annotations;
	
	public Token(){
		annotations = new HashMap<Class<Annotation<?>>,Annotation<?>>();
	}
	
	public <T> void set(Class<? extends Annotation<T>> a, T v) {
		set(Annotations.get(a,v));
	}
	
	@SuppressWarnings("unchecked")
	public void set(Annotation<?> a) {
		annotations.put((Class<Annotation<?>>)a.getClass(), a);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<? extends Annotation<T>> k) {
		Annotation<?> a = annotations.get(k);
		if(a == null) {
			return null;
		} else {
			return (T)a.getValue();
		}
	}
	
	public String get(String k) {
		try {
			return annotations.get(Class.forName(k)).getValue().toString();	
		} catch (NullPointerException e ) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
		
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for(Annotation<?> a : annotations.values()) {
			sb.append(a.toString());
			sb.append(", ");
		}
		sb.append("] ");
		
		return sb.toString();
	}
	
	
	public <T> void addAnnotationToCollection(Class<? extends Annotation<T>> k, Collection<String> list, String prefix) {
		try {
			annotations.get(k).addToCollection(list, prefix);
		} catch (OperationNotSupportedException e) {

		}
	}
}
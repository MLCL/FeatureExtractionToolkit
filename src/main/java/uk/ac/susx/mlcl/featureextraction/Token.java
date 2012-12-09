/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;

import javax.naming.OperationNotSupportedException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The Token class which holds a map of annotations and includes their
 * methods of manipulation
 * @author Simon Wibberley
 */
public class Token {

	private final Map<Class<? extends Annotation<?>>, Annotation<?>> annotations;

	public Token() {
		annotations = new HashMap<Class<? extends Annotation<?>>, Annotation<?>>();
	}

	public final <T> void setAnnotation(Class<? extends Annotation<T>> a, T v) {
		setAnnotation(Annotations.createAnnotation(a, v));
	}

	@SuppressWarnings("unchecked")
	public final void setAnnotation(Annotation<?> a) {
		annotations.put((Class<? extends Annotation<?>>) a.getClass(), a);
	}

	public final <T> void removeAnnotation(Class<? extends Annotation<T>> a) {
		annotations.remove(a);
	}

	public final <T> T getAnnotation(Class<? extends Annotation<T>> k) {
		@SuppressWarnings("unchecked")
		final Annotation<T> a = (Annotation<T>) annotations.get(k);
		return (a == null) ? null : a.getValue();
	}

	@SuppressWarnings("unchecked")
	public final <T> T getAnnotation(String k) {
		try {
			return getAnnotation((Class<? extends Annotation<T>>) Class.forName(k));
		} catch (ClassNotFoundException e) {
			return null;
		}

	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean first = true;
		for (Annotation<?> a : annotations.values()) {
			if (!first) {
				sb.append(",");
			}
			first = false;
			sb.append(a);
		}
		sb.append(")");
		return sb.toString();
	}

	/*
	 * Adds an annotations to a tokens collection, providing the
	 * annotations exists. Used by a feature function.
	 */
	public final <T> void addAnnotationToCollection(
	Class<? extends Annotation<T>> k,
	Collection<? super String> list,
	String prefix) {
		try {
			if (!annotations.containsKey(k))
				throw new IllegalArgumentException("No such element " + k);
			annotations.get(k).addToCollection(list, prefix);
		} catch (OperationNotSupportedException e) {
		} catch (IllegalArgumentException e) {
		}
	}

	private static HashMap<String, String> posMapping = new HashMap<String, String>() {{
		put("JJ", "J");
		put("JJN", "J");
		put("JJS", "J");
		put("JJR", "J");

		put("VB", "V");
		put("VBD", "V");
		put("VBG", "V");
		put("VBN", "V");
		put("VBP", "V");
		put("VBZ", "V");

		put("NN", "N");
		put("NNS", "N");
		put("NNP", "N");
		put("NPS", "N");
		put("NP", "N");

		put("RB", "RB");
		put("RBR", "RB");
		put("RBS", "RB");

		put("DT", "DET");
		put("WDT", "DET");

		put("IN", "CONJ");
		put("CC", "CONJ");

		put("PRP", "PRON");
		put("PRP$", "PRON");
		put("WP", "PRON");
		put("WP$", "PRON");

		put(".", "PUNCT");
		put(",", "PUNCT");
		put(":", "PUNCT");
		put(";", "PUNCT");
		put("'", "PUNCT");
		put("\"", "PUNCT");
		put("'", "PUNCT");
	}};

	/**
	 * Converts the provided PoS tag to a coarser representation
	 * @param in
	 * @return one of (N,V,J,RB,DET,CONJ,PRON,PUNCT, UNK)
	 */
	public static String coarsifyPoSTag(String in) {
		if (posMapping.containsKey(in.toUpperCase())) {
			return posMapping.get(in.toUpperCase());
		} else{
			return "UNK";
		}
	}
}
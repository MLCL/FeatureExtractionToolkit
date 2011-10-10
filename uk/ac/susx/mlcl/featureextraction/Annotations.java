package uk.ac.susx.mlcl.featureextraction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.naming.OperationNotSupportedException;


public class Annotations {

	private Annotations() {
		
	}
	
	public static <T> Annotation<T> get(Class<? extends Annotation<T>> cls, T value) {
		
		Annotation<T> a = null;
		try {
			a = cls.newInstance();
			a.setValue(value);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		return a;
	}
	
	public static abstract class AbstractStringAnnotation extends Annotation<String> {
		
		private String value;

		@Override
		public String getValue() {
			return value;
		}
		@Override
		public void setValue(String v) {
			value = v;
		}
		
		public String toString() {
			return value;
		}
		
		public Class<String> getType() {
			return String.class;
		}
		
		public void addToCollection(Collection<String> list, String prefix) {
			list.add(prefix+value);
		}
	}
	
	public abstract static class AbstractIntegerAnnotation extends Annotation<Integer> {

		int number;
		
		@Override
		public Integer getValue() {
			return number;
		}

		@Override
		public void setValue(Integer v) {
			number = v;
		}
		
		public void addToCollection(Collection<String> list, String prefix) {
			list.add(prefix+number);
		}
		
	}
	

	public abstract static class AbstractStringListAnnotation extends Annotation<List<String>> {

		List<String> context;
		
		@Override
		public List<String> getValue() {
			return context;
		}

		@Override
		public void setValue(List<String> c) {
			context = c;			
		}

		public String toString() {
			
			StringBuilder out = new StringBuilder();
			
			for(String s : context) {
				out.append(s);
				out.append("\t");
				
			}
			
			return out.toString();
		}
		
		public void addToCollection(Collection<String> list, String prefix) {
			for(String c : context) {
				list.add(prefix+c);
			}
		}
		
	}
	
	public static abstract class AbstractSpanAnnotation extends Annotation<int[]> {

		int[] span;
		
		@Override
		public int[] getValue() {
			return span;
		}

		@Override
		public void setValue(int[] s) {
			span = s;			
		}
		
		public void addToCollection(Collection<String> list, String prefix) throws OperationNotSupportedException {
			throw new OperationNotSupportedException();
		}
		
		public String toString() {
			return Arrays.toString(span);
		}

	}
	
	public static class CharAnnotation extends Annotation<Character> {

		Character c;
		
		@Override
		public Character getValue() {
			
			return c;
		}

		@Override
		public void setValue(Character v) {
			c = v;
		}
		
		@Override
		public String toString() {
			return c.toString();
		}
		
		public void addToCollection(Collection<String> list, String prefix) {
			list.add(prefix+c);
		}
	}
	
	public static class NgramSpanAnnotation extends Annotation<List<int[]>> {

		List<int[]> ngrams;
		
		@Override
		public List<int[]> getValue() {
			
			return ngrams;
		}

		@Override
		public void setValue(List<int[]> v) {
			ngrams = v;
		}
		
		@Override
		public void addToCollection(Collection<String> list, String prefix) throws OperationNotSupportedException {
			throw new OperationNotSupportedException();
		}
	}
	
	public static class ContextAnnotation extends AbstractStringListAnnotation {
	}
	
	public static class NgramAnnotation extends AbstractStringListAnnotation {
	}
	
	public static class ChunkSpanAnnotation extends AbstractSpanAnnotation {
	}
		
	public static class BaseAnnotation extends AbstractSpanAnnotation {
	}
	
	public static class IndexAnnotation extends AbstractIntegerAnnotation {
	}
	
	public static class SentenceLengthAnnotation extends AbstractIntegerAnnotation {
	}
	
	public static class WordAnnotation extends AbstractStringAnnotation {
	}
	
	public static class TokenAnnotation extends AbstractStringAnnotation {
	}
	
	public static class NERAnnotation extends AbstractStringAnnotation {
	}
	
	public static class PoSAnnotation extends AbstractStringAnnotation {
	}

	public static class ChunkAnnotation extends AbstractStringAnnotation {
	}
	
	public static class ChunkTagAnnotation extends AbstractStringAnnotation {
	}

	public static class NEAnnotation extends AbstractStringAnnotation {
	}
	
	public static class AnswerAnnotation extends AbstractStringAnnotation {
	}
	
	public static class __FeatureAnnotation extends AbstractStringListAnnotation {
	}
	
	public static class __KeyAnnotation extends AbstractStringAnnotation {
	}
	
	public static class __SpanAnnotation extends AbstractSpanAnnotation {	
	}
	
}

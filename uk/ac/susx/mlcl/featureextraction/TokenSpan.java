package uk.ac.susx.mlcl.featureextraction;

import uk.ac.susx.mlcl.featureextraction.Annotations.ChunkSpanAnnotation;

public class TokenSpan {
	
	public static boolean isInInclusiveWindow(int windowLeft, int windowRight, int tokenLeft, int tokenRight, int i, int len) {
		int from = Math.max(tokenLeft - Math.abs(windowLeft), 0);
		int to = Math.min(windowRight + tokenRight, len);
		//System.err.println(from + " " + to);
		return i >= from && i < to;		
	}
	
	public static boolean isInInclusiveWindow(Sentence s, IndexToken<?> cur, int i, int[] w) {
		int[] span = cur.getSpan();
		return isInInclusiveWindow(w[0], w[1], span[0], span[1], i, s.size());		
	}
	
	public static boolean isInExclusiveWindow(int tokenLeft, int tokenRight, int indexLeft, int indexRight, int i) {
		return (tokenRight <= indexLeft && i == tokenRight-1) || (indexRight <= tokenLeft && i == tokenLeft);
	}
	
	public static boolean isInExclusiveWindow(Sentence s, IndexToken<?> index, int i) {
		Token token = s.get(i);
		int[] tSpan = token.get(ChunkSpanAnnotation.class);
		int[] iSpan = index.getSpan();
		
		return isInExclusiveWindow(tSpan[0], tSpan[1], iSpan[0], iSpan[1], i);
	}

	
}

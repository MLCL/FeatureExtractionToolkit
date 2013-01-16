package uk.ac.susx.mlcl.strings;

/**
 * Copyright
 * User: mmb28
 * Date: 16/01/2013
 * Time: 10:22
 */
public class ConllSentenceSplitter extends TextSplitter {

	String[] sentences;

	public ConllSentenceSplitter() {
		start = 0;
		end = -1;
	}

	public ConllSentenceSplitter(CharSequence document) {
		start = 0;
		end = -1;
		sentences = document.toString().split("[\\r?\\n]{2,}");
	}

	@Override
	public boolean hasNext() {
		return end < sentences.length-1;
	}

	@Override
	public String next() {
		return sentences[++end];
	}

	@Override
	public TextSplitter copyForText(String newLineDelim, CharSequence text) {
//		Matcher m = Pattern.compile(newLineDelim, Pattern.DOTALL).matcher(text);
		return new ConllSentenceSplitter(text);
	}
}

package uk.ac.susx.mlcl.strings;

import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * Copyright
 * User: mmb28
 * Date: 04/01/2013
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class TextSplitter implements Iterator<String> {

	Matcher matcher;
	CharSequence text;
	int start;
	int end;

	@Override
	public boolean hasNext() {
		return matcher.find();
	}

	@Override
	public String next() {
		end = matcher.start();
		String result = text.subSequence(start, end).toString();
		start = matcher.end();
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Can't remove from this iterator!");
	}


	public abstract TextSplitter copyForText(String newLineDelim, CharSequence text);

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows the individual lines within a char sequence to be read
 *
 * @author jp242
 */
public class NewlineSplitter extends TextSplitter {

	public NewlineSplitter(Matcher matcher, CharSequence text) {
		this.matcher = matcher;
		this.text = text;
		start = 0;
		end = 0;
	}

	public NewlineSplitter() {
		start = 0;
		end = 0;
	}

	@Override
	public TextSplitter copyForText(String newLineDelim, CharSequence text) {
		Matcher m = Pattern.compile(newLineDelim).matcher(text);
		return new NewlineSplitter(m, text);
	}
}

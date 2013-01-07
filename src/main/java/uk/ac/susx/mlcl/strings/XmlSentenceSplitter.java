package uk.ac.susx.mlcl.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Returns a single sentence as output by Stanford CoreNLP
 *
 * @author Miroslav Batchkarov
 * @version 04/01/2013
 */
public class XmlSentenceSplitter extends TextSplitter {

	StringBuilder sb;
	private static final String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"<?xml-stylesheet href=\"CoreNLP-to-HTML.xsl\" type=\"text/xsl\"?>" +
	"\n<root>\n";

	public XmlSentenceSplitter(Matcher matcher, CharSequence text) {
		this.matcher = matcher;
		this.text = text;
		start = 0;
		end = 0;
		sb = new StringBuilder();
	}

	public XmlSentenceSplitter() {
		start = 0;
		end = 0;
		sb = new StringBuilder();
	}

	@Override
	public String next() {
		sb.setLength(0);//wipe the buffer
		sb.append(preamble).append(matcher.group(1)).append("\n</root>");
		return sb.toString();
	}


	@Override
	public TextSplitter copyForText(String newLineDelim, CharSequence text) {
		Matcher m = Pattern.compile(newLineDelim, Pattern.DOTALL).matcher(text);
		return new XmlSentenceSplitter(m, text);
	}
}

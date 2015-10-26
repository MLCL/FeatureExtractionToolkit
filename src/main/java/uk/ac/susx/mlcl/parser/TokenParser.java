/*
 * Copyright (c) 2010-2013, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;
import uk.ac.susx.mlcl.featureextraction.ContextWindowStringConverter;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.ContextWindowsFeatureConstraint;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.DisjointFeatureConstraint;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;
import uk.ac.susx.mlcl.featureextraction.featurefunction.TokenFeatureFunction;
import uk.ac.susx.mlcl.util.IntSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hiam20
 */
public class TokenParser extends AbstractParser {

	private static final Logger LOG =
	Logger.getLogger(TokenParser.class.getName());

	@Override
	protected String newLineDelim() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	protected Map<Object, Object> rawTextParse(CharSequence text) throws ModelNotValidException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public class TokenConfig extends AbstractParserConfig {

		private static final long serialVersionUID = 1L;

		@Parameter(names = {"-lc", "--useLowercase"},
		description = "convert all strings to lower-case")
		private boolean useLowercase = false;

		@Parameter(names = {"-normnum", "--useNormalisedNumbers"},
		description = "replace all digits with a canonicalised number tag")
		private boolean useNormalisedNumbers = false;

		@Parameter(names = {"-cw", "--contextWindow"},
		converter = ContextWindowStringConverter.class,
		description = "in the form \"-LEFT+RIGHT\" for LEFT tokens to the left and RIGHT tokens to the right")
		private IntSpan contextWindow = new IntSpan(-5, 5);

		@Parameter(names = {"-p", "--punctuation"},
		description = "Produce punctuation characters as tokens, in addition to words.")
		private boolean producePunctuation = false;

		public boolean isUseLowercase() {
			return useLowercase;
		}

		public boolean isUseNormalisedNumbers() {
			return useNormalisedNumbers;
		}

		public IntSpan getContextWindow() {
			return contextWindow;
		}

		public boolean isProducePunctuation() {
			return producePunctuation;
		}

	}

	private TokenConfig config;

	private static final String LEFT_TERM_FEAT_PREFIX = "lT:";

	private static final String RIGHT_TERM_FEAT_PREFIX = "rT:";

	private static final Pattern NUMBER_REGEX = Pattern.compile(
	"[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?");

	private static final String NORMALISED_NUMBER_STR = "N";

	private static final String WHITESPACE = "\\p{javaWhitespace}";

	private static final String LETTERS = "\\p{Lo}\\p{Ll}\\p{Lu}\\p{Lt}\\p{Lm}";

	private static final String DIGIT = "\\p{Nd}";

	private static final Pattern SPLIT_REGEX = Pattern.compile(
	"([" + WHITESPACE + "]+|[^" + LETTERS + DIGIT + "])");

	public static void main(String[] args) {
		TokenParser mp = new TokenParser();
		mp.init(args);
		mp.parse();
	}

	public TokenParser() {
	}

	@Override
	protected String getOutPath() {
		String outPath = config.getOutPath();

		if (config.isUseLowercase()) {
			outPath += "-lc";
		}
		if (config.isUseNormalisedNumbers()) {
			outPath += "-nn";
		}
		outPath += "-cw" + config.getContextWindow().left + config.getContextWindow().right;

		return outPath;
	}

	@Override
	protected Map<Object, Object> loadPreparsedEntry(String entry) {
		throw new IllegalStateException("Loading pre-parsed text not implemented for this type");
	}

	@Override
	protected List<Sentence> annotate(Map<Object, Object> map){
		//todo assumes the map contains a single string as a value
		String entry = (String) new ArrayList<Object>(map.values()).get(0);
		final Sentence annotated = new Sentence();
		try {

			final Matcher wsm = SPLIT_REGEX.matcher(entry);
			int tokenStart = 0;
			int index = 0;
			boolean finished = false;
			while (!finished) {

				final int tokenEnd, delimStart, delimEnd;
				if (wsm.find()) {
					tokenEnd = wsm.start();
					delimStart = wsm.start();
					delimEnd = wsm.end();
				} else {
					tokenEnd = entry.length();
					delimStart = entry.length();
					delimEnd = entry.length();
					finished = true;
				}

				if (tokenStart < tokenEnd) {

					String token = entry.substring(tokenStart, tokenEnd);

					if (config.isUseLowercase())
						System.out.println("Lower-casing");
					token = token.toLowerCase();

					if (config.isUseNormalisedNumbers()) {
						final Matcher nm = NUMBER_REGEX.matcher(token);
						token = NUMBER_REGEX.matcher(token).replaceAll(
						NORMALISED_NUMBER_STR);
					}
					final Token t = new Token();
					t.setAnnotation(Annotations.TokenAnnotation.class, token);
					t.setAnnotation(Annotations.IndexAnnotation.class, index);

					IndexToken<CharSequence> key = new IndexToken<CharSequence>(
					new IntSpan(index, index), Annotations.TokenAnnotation.class);
					annotated.add(t);
					annotated.addKey(key);

					++index;

				}

				if (config.isProducePunctuation() && delimStart < delimEnd) {
					String delim = entry.substring(delimStart, delimEnd);

					if (!delim.trim().isEmpty()) {
						final Token t = new Token();
						t.setAnnotation(Annotations.TokenAnnotation.class, delim);
						t.setAnnotation(Annotations.IndexAnnotation.class, index);

						IndexToken<CharSequence> key = new IndexToken<CharSequence>(
						new IntSpan(index, index), Annotations.TokenAnnotation.class);
						annotated.add(t);
						annotated.addKey(key);

						++index;
					}
				}

				tokenStart = tokenEnd + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.log(Level.SEVERE, null, e);
		}


		applyFeatureFactory(getFeatureFactory(), annotated);

		List<Sentence> ret = new ArrayList<Sentence>();
		ret.add(annotated);
		return ret;
	}

	@Override
	protected FeatureFactory buildFeatureFactory() {

		FeatureFactory featureFactory = new FeatureFactory();

		TokenFeatureFunction btff = new TokenFeatureFunction();
		btff.addConstraint(new ContextWindowsFeatureConstraint(
		new IntSpan(config.getContextWindow().left, 0)));
		btff.addConstraint(new DisjointFeatureConstraint());
		btff.setPrefix(LEFT_TERM_FEAT_PREFIX);
		featureFactory.addFeature("leftTokenFeature", btff);

		TokenFeatureFunction atff = new TokenFeatureFunction();
		atff.addConstraint(new ContextWindowsFeatureConstraint(
		new IntSpan(0, config.getContextWindow().right)));
		atff.addConstraint(new DisjointFeatureConstraint());
		atff.setPrefix(RIGHT_TERM_FEAT_PREFIX);
		featureFactory.addFeature("rightTokenFeature", atff);

		return featureFactory;
	}

	@Override
	public void init(String[] args) {
		config = new TokenConfig();
		config.load(args);
//		setSplitter(new NewlineStringSplitter());
	}

	@SuppressWarnings("unchecked")
	@Override
	public TokenConfig config() {
		return config;
	}


	@Override
	protected void setKeyConstraints() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


}

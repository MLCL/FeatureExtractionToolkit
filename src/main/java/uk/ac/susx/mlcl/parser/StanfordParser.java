/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

/**
 * @author jp242
 */
public abstract class StanfordParser extends AbstractParser {

	private static final Logger LOG =
	Logger.getLogger(AbstractParser.class.getName());


	protected abstract static class StanConfig extends AbstractParserConfig {

		private static final long serialVersionUID = 1L;

		@Parameter(names = {"-tok", "--tokenizeText"},
		description = "Tokenize text")
		private boolean tokenText = false;

		@Parameter(names = {"-posTag", "--posTagSentence"},
		description = "Fully split tokenize and pos tag")
		private boolean posTagText = true;

		@Parameter(names = {"-ss", "--SplitSentence"},
		description = "Just split sentence")
		private boolean splitSent = false;


		@Parameter(names = {"-lem", "--useLemma"},
		description = "Lemmatize")
		private boolean useLemma = false;

		@Parameter(names = {"-cpos", "--useCoarsePoS"},
		description = "Lemmatize")
		private boolean useCoarsePos = false;

		public boolean isUseCoarsePos() {
			return useCoarsePos;
		}

		public boolean isUseLemma() {
			return useLemma;
		}

		public boolean tokenize() {
			return tokenText;
		}

		public boolean posTag() {
			return posTagText;
		}

		public boolean splitSent() {
			return splitSent;
		}

	}

	//    private StanfordRawTextPreProcessor preprocessor;
	private BlockingQueue<StanfordCoreNLP> pipelines;

	private static final String POS_DELIMITER = "/";

	private static final String NEW_LINE_DELIM = "\n";

	private static final String TOKEN_DELIM = " ";

	private static final String POS_PREFIX = "POS:";

	public String getPosDelim() {
		return POS_DELIMITER;
	}

	public String getTokenDelim() {
		return TOKEN_DELIM;
	}

	public String getPosPrefix() {
		return POS_PREFIX;
	}


	@Override
	protected String newLineDelim() {
		return NEW_LINE_DELIM;
	}

	public void initPreProcessor() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipelines = new LinkedBlockingDeque<StanfordCoreNLP>();
		for (int i = 0; i < config().getNumCores(); i++) {
			try {
				this.pipelines.put(new StanfordCoreNLP(props));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns a map between sentence id's and their tokenized, lowercased, and lemmatized text
	 * @param text A raw document
	 * @return
	 */
	@Override
	protected Map<Object, Object> rawTextParse(CharSequence text) {

		StanfordCoreNLP pipeline = null;
		try {
			pipeline = pipelines.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Annotation document = new Annotation(text.toString());
		pipeline.annotate(document);
		HashMap<Object, Object> sentencesMap = new HashMap<Object, Object>();
		int id = 0;
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			StringBuilder processedText = new StringBuilder();
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				//todo this should really happen after parsing is done, because using lemmas might confuse the parser
				if (config().isUseLowercaseEntries()) {
					word = word.toLowerCase();
					lemma = lemma.toLowerCase();
				}
				if (config().isUseLemma()) {
					word = lemma;
				}
				processedText.append(word).append(POS_DELIMITER).append(lemma).append(POS_DELIMITER).append(pos).append(TOKEN_DELIM);
			}
			sentencesMap.put(id, processedText.toString());
			id++;
		}
		try {
			pipelines.put(pipeline);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sentencesMap;
	}

	@Override
	public String getOutPath() {

		String outPath = super.getOutPath();

		outPath += (config().posTag()) ? "-pos" : "";

		outPath += (config().tokenize()) ? "-tok" : "";

		outPath += (config().splitSent()) ? "-ss" : "";

		return outPath;
	}

	@Override
	protected abstract StanConfig config();

}

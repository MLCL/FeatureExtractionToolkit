package uk.ac.susx.mlcl.parser;


import com.beust.jcommander.Parameter;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.symbol.SymbolException;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.core.syntaxgraph.edge.Edge;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.InvalidEntryException;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;
import uk.ac.susx.mlcl.featureextraction.featurefunction.DependencyFeatureFunction;
import uk.ac.susx.mlcl.strings.NewlineReader;
import uk.ac.susx.mlcl.util.IntSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author jp242
 */
public class StanPoSMaltDepParser extends StanfordParser {

	private static final Logger LOG =
	Logger.getLogger(AbstractParser.class.getName());

	public static class StanMaltConfig extends StanConfig {

		private static final long serialVersionUID = 1L;

		@Parameter(names = {"-dr", "--depRels"},
		description = "Array of dependancy relations to extract.")
		private List<String> depList = new ArrayList<String>();

		@Parameter(names = {"-hdr", "--headDepRels"},
		description = "Array of head dependancy relations to extract.")
		private List<String> hDepList = new ArrayList<String>();

		@Parameter(names = {"-modN", "--modelName"},
		required = true,
		description = "Name of the dependancy parser model")
		private String modN = null;

		public List depList() {
			return depList;
		}

		public List hDepList() {
			return hDepList;
		}

		public String modeName() {
			return modN;
		}
	}

	private StanMaltConfig config;
	private BlockingQueue<MaltParserWrapper> parsers;// maltPar;

	private static final String PARSED_DELIM = "-]";

	@Override
	protected Sentence annotate(String entry, Object preprocessor) {

		final Sentence annotated = new Sentence();

		if (!config().isUseTokenAsBase() && !config().isUseChunkAsBase()) {
			throw new RuntimeException(
			"useTokenAsBase is off, there must be base entries!");
		}

		if (config.depList == null && config().hDepList == null) {
			throw new RuntimeException(
			"No dependancy relations specified!");
		}


		try {
			processEntry(entry, annotated, (MaltParserWrapper) preprocessor);
		} catch (MaltChainedException e) {
			e.printStackTrace();
		}

		if (annotated.size() <= 1) {
			throw new InvalidEntryException("single entry sentence!");
		}

		applyFeatureFactory(getFeatureFactory(), annotated);

		return annotated;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		StanPoSMaltDepParser sp = new StanPoSMaltDepParser();
		sp.init(args);
		long start = System.currentTimeMillis();
		sp.parse();
		System.out.println("Time (ms) = " + (System.currentTimeMillis() - start));
	}

	@Override
	protected FeatureFactory buildFeatureFactory() {
		FeatureFactory featurefactory = super.buildFeatureFactory();
		for (Iterator<String> it = config.depList.iterator(); it.hasNext(); ) {
			String dep = it.next();
			DependencyFeatureFunction fn = new DependencyFeatureFunction(dep, true);
			fn.setPrefix(dep + "-DEP:");
			featurefactory.addFeature("Dependency feature function (dep)" + dep, fn);
		}

		for (Iterator<String> it = config.hDepList.iterator(); it.hasNext(); ) {
			String dep = it.next();
			DependencyFeatureFunction fn = new DependencyFeatureFunction(dep, false);
			fn.setPrefix(dep + "-HEAD:");
			featurefactory.addFeature("Dependency feature function (head)" + dep, fn);
		}

		return featurefactory;
	}

	@Override
	public void init(String[] args) {
		config = new StanMaltConfig();
		config.load(args);
		super.initPreProcessor();
		int n = config.getNumCores();
		this.parsers = new LinkedBlockingDeque<MaltParserWrapper>(n);
		for (int i = 0; i < n; i++) {
			MaltParserWrapper maltPar = new MaltParserWrapper(super.getPosDelim(), config.modeName(), i);
			parsers.add(maltPar);
		}
	}

	protected StanMaltConfig config() {
		return config;
	}

	@Override
	protected RawTextPreProcessorInterface getPreprocessor() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getOutPath() {
		String outpath = super.getOutPath();

		if (config().depList() != null) {
			for (Iterator<String> it = config.depList.iterator(); it.hasNext(); ) {
				String dep = it.next();
				outpath += "-" + dep;
			}
		}
		if (config().hDepList() != null) {
			for (Iterator<String> it = config.hDepList.iterator(); it.hasNext(); ) {
				String dep = it.next();
				outpath += "-" + dep;
			}
		}

		if (config().depList() != null) {
			for (Iterator<String> it = config.depList.iterator(); it.hasNext(); ) {
				String dep = it.next();
				outpath += "-" + dep;
			}
		}
		if (config().hDepList() != null) {
			for (Iterator<String> it = config.hDepList.iterator(); it.hasNext(); ) {
				String dep = it.next();
				outpath += "-" + dep;
			}
		}

		return outpath;
	}

	private void processEntry(String entry, Sentence annotated, MaltParserWrapper maltPar) throws MaltChainedException {
		String[] splitSent = entry.split(PARSED_DELIM);
		DependencyStructure graph = null;
		if (config.depList() != null || config().hDepList() != null) {
			//only parse if dependency relations have been requested
//			System.out.println("Parsing in thread " + Thread.currentThread());
			try {
				graph = maltPar.toDependencyStructure(splitSent, false);
			} catch (SymbolException ex) {
				//for some reason malt parser sometimes tries to lookup PoS tags instead of tokens in its table and dies
			}
		}


		for (int i = 0; i < splitSent.length; i++) {
			Token t = new Token();
			String[] tokPos = splitSent[i].split("\t");
			String pos = config.isUseCoarsePos() ? Token.coarsifyPoSTag(tokPos[3]) : tokPos[3];
			t.setAnnotation(Annotations.TokenAnnotation.class, tokPos[1] + getPosDelim() + pos);
			t.setAnnotation(Annotations.OriginalTokenAnnotation.class, tokPos[1]);
			t.setAnnotation(Annotations.PoSAnnotation.class, tokPos[3]);
			if (config.depList() != null) {
				t.setAnnotation(Annotations.DependencyListAnnotation.class, new HashMap<String, ArrayList<String>>());
			}
			if (config.hDepList() != null) {
				t.setAnnotation(Annotations.DependencyHeadListAnnotation.class, new HashMap<String, ArrayList<String>>());
			}
			annotated.add(t);
			IndexToken<CharSequence> key = new IndexToken<CharSequence>(new IntSpan(i, i), Annotations.TokenAnnotation.class);
			annotated.addKey(key);
		}

		if (graph != null) {
			for (Edge edge : maltPar.getEdges(graph)) {
				if (maltPar.getHeadIndex(edge) - 1 >= 0 && maltPar.getDependantIndex(edge) - 1 >= 0) {
					ArrayList<String> feats = (ArrayList<String>) annotated.get(maltPar.getHeadIndex(edge) - 1).
					getAnnotation(Annotations.DependencyHeadListAnnotation.class).get(maltPar.getDepRel(edge, graph));
					if (config.hDepList() != null) {
						if (feats == null) {
							feats = new ArrayList<String>();
							feats.add(maltPar.getDependant(edge, graph));
							annotated.get(maltPar.getHeadIndex(edge) - 1).
							getAnnotation(Annotations.DependencyHeadListAnnotation.class).
							put(maltPar.getDepRel(edge, graph), feats);
						} else {
							feats.add(maltPar.getDependant(edge, graph));
						}
					}
					if (config.depList() != null) {
						feats = (ArrayList<String>) annotated.get(maltPar.getDependantIndex(edge) - 1).
						getAnnotation(Annotations.DependencyListAnnotation.class).get(maltPar.getDepRel(edge, graph));
						if (feats == null) {
							feats = new ArrayList<String>();
							feats.add(maltPar.getHead(edge, graph));
							annotated.get(maltPar.getDependantIndex(edge) - 1).
							getAnnotation(Annotations.DependencyListAnnotation.class).
							put(maltPar.getDepRel(edge, graph), feats);
						} else {
							feats.add(maltPar.getDependant(edge, graph));
						}
					}
				}
			}
		} else {
			System.err.println("Dependency graph is null");
		}
		parsers.offer(maltPar);
	}

	/**
	 * Returns the pre-processed text and the preprocessor object as an object array
	 *
	 * @param text
	 * @return
	 * @throws ModelNotValidException
	 */
	@Override
	protected Object[] rawTextParse(final CharSequence text) {
		//sentence segment, tokenize, lemmatize and PoS tag
		Object[] ret = super.rawTextParse(text);
		CharSequence processedText = (CharSequence) ret[0];
		//ret[1] is the stanford PoS tagger
		MaltParserWrapper maltPar = null;
		try {
			maltPar = parsers.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		//TODO: Re-Factor Pre-processing.

		NewlineReader nr = new NewlineReader(processedText, newLineDelim());
		StringBuffer preProcText = new StringBuffer();
		while (nr.hasLine()) {
			final String line = nr.readLine();
			String[] sentence = maltPar.formatSentenceForMaltParser(line.split(getTokenDelim()));
			try {
				String[] parsedSent = maltPar.parseTokens(sentence);
				preProcText.append(buildString(parsedSent));
			} catch (MaltChainedException ex) {
				Logger.getLogger(StanPoSMaltDepParser.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		//return the MaltParserWrapper used by this method because it contains all dependencies
		return new Object[] {preProcText.toString(), maltPar};
	}

	private String buildString(String[] tokens) {


		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append(tokens[i]);
			if (i < (tokens.length - 1)) {
				sb.append(PARSED_DELIM);
			}
		}
		sb.append(newLineDelim());

		return sb.toString();
	}
}

package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;
import org.apache.xerces.dom.DeferredElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.ac.susx.mlcl.featureextraction.InvalidEntryException;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright
 * User: mmb28
 * Date: 04/01/2013
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
public class StanfordPreprocessedParser extends AbstractParser {

	private static final String POS_DELIMITER = "/";

	public StanfordPreprocessedParser(String[] args) {
		config = new StanfordPreprocessedConfig();
		config.load(args);
	}

	/**
	 * Borrows functionality from both StanConfig and StanMaltConfig, keeping in ming the corpus has
	 * already been preprocessed
	 */
	public class StanfordPreprocessedConfig extends AbstractParserConfig {
		@Parameter(names = {"-cpos", "--useCoarsePoS"},
		description = "Whether to use coarse PoS tags or nor")
		private boolean useCoarsePos = false;

		@Parameter(names = {"-lem", "--useLemma"},
		description = "Lemmatize")
		private boolean useLemma = false;

		@Parameter(names = {"-dr", "--depRels"},
		description = "Array of dependancy relations to extract.")
		private List<String> depList = new ArrayList<String>();

		@Parameter(names = {"-hdr", "--headDepRels"},
		description = "Array of head dependancy relations to extract.")
		private List<String> hDepList = new ArrayList<String>();

		public boolean isUseCoarsePos() {
			return useCoarsePos;
		}

		private static final long serialVersionUID = 1L;

		public boolean isUseLemma() {
			return useLemma;
		}

		public List depList() {
			return depList;
		}

		public List hDepList() {
			return hDepList;
		}

	}

	private StanfordPreprocessedConfig config;

	protected StanfordPreprocessedConfig config() {
		return config;
	}

	@Override
	protected List<Sentence> annotate(Map<Object, Object> map) {
		List<Sentence> annotatedSentences = new ArrayList<Sentence>();

		if (!config().isUseTokenAsBase() && !config().isUseChunkAsBase()) {
			throw new RuntimeException(
			"useTokenAsBase is off, there must be base entries!");
		}

		if (config.depList == null && config().hDepList == null) {
			throw new RuntimeException(
			"No dependancy relations specified!");
		}


		for (Object sentString : map.values()) {
			Sentence sentObject = processEntry((String) sentString);
			if (sentObject.size() < 1) {
				throw new InvalidEntryException("empty sentence!");
			}
			applyFeatureFactory(getFeatureFactory(), sentObject);
			annotatedSentences.add(sentObject);
		}
		return annotatedSentences;
	}

	private Sentence processEntry(String sentenceStr) {
		Sentence annotatedSent = new Sentence();

		//todo DOM may be too slow
		//todo this method begs for a unit test
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(sentenceStr)));
			doc.getDocumentElement().normalize();

			//read off the tokens and their lemmas, pos tags, etc
			NodeList nList = doc.getElementsByTagName("token");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				String word, pos, lemma;
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					word = getTagValue("word", eElement);
					lemma = getTagValue("lemma", eElement);
					pos = getTagValue("POS", eElement);
					pos = config.isUseCoarsePos() ? Token.coarsifyPoSTag(pos) : pos;

					if (config().isUseLowercaseEntries()) {
						word = word.toLowerCase();
						lemma = lemma.toLowerCase();
					}
					if (config().isUseLemma()) {
						word = lemma;
					}

					Token t = new Token();
					t.setAnnotation(Annotations.OriginalTokenAnnotation.class, word);
					t.setAnnotation(Annotations.TokenAnnotation.class, word + POS_DELIMITER + pos);
					t.setAnnotation(Annotations.PoSAnnotation.class, pos);
					annotatedSent.add(t);
				}
			}

			nList = doc.getElementsByTagName("dep");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getParentNode().getNodeName() != "basic-dependencies")
					continue;
				String depRelation = ((DeferredElementImpl) nNode).getAttribute("type");
				Node governor = ((DeferredElementImpl) nNode).getElementsByTagName("governor").item(0);
				Node dependent = ((DeferredElementImpl) nNode).getElementsByTagName("dependent").item(0);
				int govIndex = Integer.parseInt(((DeferredElementImpl) governor).getAttribute("idx")) - 1;
				String govWord = governor.getTextContent();
				int depIndex = Integer.parseInt(((DeferredElementImpl) dependent).getAttribute("idx")) - 1;
				String depWord = dependent.getTextContent();
				if (govIndex > -1 && depIndex > -1) {//the dependency is well-formed

					ArrayList<String> feats = (ArrayList<String>) annotatedSent.
					get(govIndex).
					getAnnotation(Annotations.DependencyHeadListAnnotation.class).
					get(depRelation);

					if (config.hDepList() != null) {
						if (feats == null) {
							feats = new ArrayList<String>();
							feats.add(depWord);
							annotatedSent.get(govIndex).
							getAnnotation(Annotations.DependencyHeadListAnnotation.class).
							put(depRelation, feats);
						} else {
							feats.add(depWord);
						}
					}
					if (config.depList() != null) {
						feats = (ArrayList<String>) annotatedSent.
						get(depIndex).
						getAnnotation(Annotations.DependencyListAnnotation.class).
						get(depRelation);

						if (feats == null) {
							feats = new ArrayList<String>();
							feats.add(govWord);
							annotatedSent.get(depIndex).
							getAnnotation(Annotations.DependencyListAnnotation.class).
							put(depRelation, feats);
						} else {
							feats.add(depWord);
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (SAXException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		return annotatedSent;
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = nlList.item(0);
		return nValue.getNodeValue();
	}


	@Override
	protected String newLineDelim() {
		return "<sentence id=\"\\d+?\">(.+?)</sentence>";
	}

	@Override
	/**
	 * Does nothing. Assumes text is the Stanford-privided XML representation of a single sentence
	 */
	protected Map<Object, Object> rawTextParse(CharSequence text) throws ModelNotValidException {
		Map<Object, Object> res = new HashMap<Object, Object>(1, 1f);
		res.put(0, text);
		return res;
	}

	@Override
	public void init(String[] args) {
		//nothing to do
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		StanfordPreprocessedParser sp = new StanfordPreprocessedParser(args);
		sp.init(args);
		long start = System.currentTimeMillis();
		sp.parse();
		System.out.println("Time (ms) = " + (System.currentTimeMillis() - start));
	}
}

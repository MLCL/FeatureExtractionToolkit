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
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author jp242
 */
public abstract class StanfordParser extends AbstractParser {

    private static final Logger LOG =
            Logger.getLogger(AbstractParser.class.getName());

    protected abstract static class StanConfig extends AbstractParserConfig {

        private static final long serialVersionUID = 1L;

//        @Parameter(names = {"-mloc", "--modelLocation"},
//                required = true,
//                description = "The location of the model needed to parse the input text")
//        private String modelLocation;

        @Parameter(names = {"-tok", "--tokenizeText"},
                description = "Tokenize text")
        private boolean tokenText = false;

        @Parameter(names = {"-posTag", "--posTagSentence"},
                description = "Fully split tokenize and pos tag")
        private boolean posTagText = true;

        @Parameter(names = {"-ss", "--SplitSentence"},
                description = "Just split sentence")
        private boolean splitSent = false;

        @Parameter(names = {"-lc", "--useLowercase"},
                description = "convert all strings to lower-case")
        private boolean useLowercase = false;

        @Parameter(names = {"-lem", "--useLemma"},
                description = "Lemmatize")
        private boolean useLemma = false;

        public boolean isUseLowercase() {
            return useLowercase;
        }

        public boolean isUseLemma() {
            return useLemma;
        }

//        public boolean modelLocValid() {
//            return modelLocation == null;
//        }
//
//        public String modelLoc() {
//            return modelLocation;
//        }

//        public boolean isValidModel() {
//            File file = new File(modelLocation);
//            return file.isFile();
//        }

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
    private StanfordCoreNLP pipeline;

    private static final String POS_DELIMITER = "/";

    private static final String NEW_LINE_DELIM = "\n";

    private static final String TOKEN_DELIM = " ";

    private static final String POS_PREFIX = "POS:";

    private static final String LEMMA_PREFIX = "LEM:";

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
        this.pipeline = new StanfordCoreNLP(props);
        System.out.println("initialised");
    }

    @Override
    protected CharSequence rawTextParse(CharSequence text) throws ModelNotValidException {
        StringBuilder processedText = new StringBuilder();

        Annotation document = new Annotation(text.toString());
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

//                todo this must happen after parsing is done
//                if(config().isUseLowercase()){
//                    word = word.toLowerCase();
//                    lemma = lemma.toLowerCase();
//                }
//                if(config().isUseLemma()){
//                    word = lemma;
//                }
                processedText.append(word).append(POS_DELIMITER).append(lemma).append(POS_DELIMITER).append(pos).append(TOKEN_DELIM);
            }
            processedText.append(NEW_LINE_DELIM);
        }
        return processedText;
    }

    @Override
    protected FeatureFactory buildFeatureFactory() {
        FeatureFactory featurefactory = super.buildFeatureFactory();
        return featurefactory;
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

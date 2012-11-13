/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;
import uk.ac.susx.mlcl.strings.NewlineStringSplitter;
import uk.ac.susx.mlcl.strings.StringSplitter;

/**
 *
 * @author jp242
 */
public abstract class StanfordParser extends AbstractParser{
    
    private static final Logger LOG =
            Logger.getLogger(AbstractParser.class.getName());
    
    protected abstract static class StanConfig extends AbstractParserConfig {
        
        private static final long serialVersionUID = 1L;

        @Parameter(names = {"-mloc", "--modelLocation"},
        required = true,
        description = "The location of the model needed to parse the input text")
        private String modelLocation;
        
        @Parameter(names = {"-tok", "--tokenizeText"},
        description = "Tokenize text")
        private boolean tokenText = false;
        
        @Parameter(names = {"-posTag", "--posTagSentence"},
        description = "Fully split tokenize and pos tag")
        private boolean posTagText = true;
        
        @Parameter(names = {"-ss", "--SplitSentence"},
        description = "Just split sentence")
        private boolean splitSent = false;
        
        public boolean modelLocValid(){
            return modelLocation == null;
        }
        
        public String modelLoc(){
            return modelLocation;
        }
            
        public boolean isValidModel(){
            File file = new File(modelLocation);
            return file.isFile();
        }
        
        public boolean tokenize(){
            return tokenText;
        }
        
        public boolean posTag(){
            return posTagText;
        }
        
        public boolean splitSent(){
            return splitSent;
        }

    }      
    
    private StanfordRawTextPreProcessor preprocessor;
    
    private static final String POS_DELIMITER = "/";
    
    private static final String NEW_LINE_DELIM = "\n";
    
    private static final String TOKEN_DELIM = " ";
    
    private static final String POS_PREFIX = "POS:";
    
    public String getPosDelim(){
        return POS_DELIMITER;
    }
    
    public String getTokenDelim(){
        return TOKEN_DELIM;
    }
    
    public String getPosPrefix(){
        return POS_PREFIX;
    }
    
        
    @Override
    protected String newLineDelim(){
        return NEW_LINE_DELIM;
    }

    public void initPreProcessor() {
        try {
            preprocessor = new StanfordRawTextPreProcessor(config().modelLoc(), newLineDelim(), getTokenDelim());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StanfordParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StanfordParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected RawTextPreProcessorInterface preProcessor() {
        return preprocessor;
    }
    
    @Override
    protected CharSequence rawTextParse(CharSequence text) throws ModelNotValidException{
        if(!config().isValidModel() && config().posTag()){
            throw new ModelNotValidException("The location to the PoStagger model is either invalid or null");
        }
        
        String procText = null;
        if(config().posTag()){
            procText = (String) preProcessor().posTagText(text);
            return procText;
        }
        
        if(config().splitSent()){
            List<CharSequence> sentences = preProcessor().splitSentences(text.toString());
            for(CharSequence sent : sentences){
                    procText += sent + NEW_LINE_DELIM;
            }
            return procText;
        }
        
        if(config().tokenize()){
            procText = (String) preProcessor().tokenizeText(text);
            return procText;
        }
        
        return procText;
     }
    
    @Override
    protected FeatureFactory buildFeatureFactory() {
        FeatureFactory featurefactory = super.buildFeatureFactory();
        return featurefactory;
    }
    
    @Override
    public String getOutPath(){
        
        String outPath = super.getOutPath();
        
        outPath += (config().posTag()) ? "-pos" : "";
        
        outPath += (config().tokenize()) ? "-tok" : "";
        
        outPath += (config().splitSent()) ? "-ss" : "";
        
        return outPath;
    }
        
    @Override
    protected abstract StanConfig config();
  
}

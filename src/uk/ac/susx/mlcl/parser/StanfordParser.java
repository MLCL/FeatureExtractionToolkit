/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;

/**
 *
 * @author jp242
 */
public class StanfordParser extends AbstractParser{

    @Override
    protected RawTextPreProcessor preProcessor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String newLineDelim() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
    protected static class StanConfig extends AbstractParserConfig {
        
        @Parameter(names = {"-mloc", "--modelLocation"},
        description = "The location of the model needed to parse the input text")
        private String modelLocation = null;
        
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
    
    public StanfordParser(){}
    
    private StanConfig config;
    
    private static final String POS_DELIMITER = "_";

    @Override
    protected Sentence annotate(String entry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void setKeyConstraints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected FeatureFactory buildFeatureFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected AbstractParserConfig config() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(String[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

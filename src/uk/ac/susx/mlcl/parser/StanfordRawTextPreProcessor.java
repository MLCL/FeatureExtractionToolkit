/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import java.io.FileReader;
import java.util.List;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * An implementation of a parser using the Standford Tagger.
 * @author jp242
 */
public class StanfordRawTextPreProcessor implements RawTextPreProcessorInterface{
    
    
    
    private MaxentTagger tagger;
    private String sentDelim;
    private final String tokDelim;
    
    public StanfordRawTextPreProcessor(String posTaggerModelLocation, String sentDelim,String tokDelim) throws ClassNotFoundException, IOException{
        tagger = new MaxentTagger(posTaggerModelLocation);
        this.sentDelim = sentDelim;
        this.tokDelim = tokDelim;
    }
    
    /**
     * Opens the document at the given path and splits into sentences.
     * @param docLoc absolute path to input document
     * @return Outputs DocumentPreprocessor object containing split sentences.
     */
    @Override
    public List<List<HasWord>> splitSentences(CharSequence text){
        StringReader sr = new StringReader(text.toString());
        DocumentPreprocessor dp = new DocumentPreprocessor(sr);
        return (List<List<HasWord>>) dp;
    }

    /**
     * PoS tags the given input sentence
     */
    @Override
    public String posTagSentence(List sentence) {
        return tagger.tagSentence(sentence).toString();
    }
    
    public String posTagString(String sentence){
        return tagger.tagString(sentence);
    }

    /**
     * @param sentence String representation of sentence
     * @return ArrayList of tokens represented as CoreLabel objects
     */
    @Override
    public List<List<HasWord>> tokenizeSentence(CharSequence sentence) {
      return MaxentTagger.tokenizeText(new StringReader(sentence.toString()));
    }

    @Override
    public String posTagSentence(CharSequence sentence) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List tokenizeSentence(List sentence) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CharSequence posTagText(CharSequence text) {
        StringReader sr = new StringReader(text.toString());
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(sr);
        List<ArrayList<TaggedWord>> taggSents = tagger.process(sentences);
        String taggedText = "";
        for(ArrayList<TaggedWord> sent : taggSents){
            taggedText += toString(sent) + sentDelim;
        }
        return taggedText;
    }

    @Override
    public CharSequence tokenizeText(CharSequence text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CharSequence groupSentence(CharSequence text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String toString(ArrayList<TaggedWord> sent) {
        Iterator it = sent.iterator();
        String stringSent = "";
        while(it.hasNext()){
            stringSent += it.next().toString() + tokDelim;
        }
        return stringSent;
    }
}

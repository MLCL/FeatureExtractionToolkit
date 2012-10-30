/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import java.util.List;

/**
 * Abstract to allow creation of parser which splits sentences,  
 * PoS tags and groups raw text.
 * @author jp242
 */
public interface RawTextPreProcessor {
    
    // Takes a file path or text as input and outputs List of sentences
    public List splitSentences(CharSequence doc);
    
    // Takes a sentence as input and outputs PoS tagged sentence
    public CharSequence posTagSentence(CharSequence sentence);
    
    public CharSequence posTagSentence(List sentence);
    
    // Takes a list of sentences and outputs a body of pos tagged text as output 
    public CharSequence posTagText(CharSequence text);
    
    // Takes a sentence as input and outputs the sentence, tokenised.
    public List tokenizeSentence(CharSequence sentence);
    
    public List tokenizeSentence(List sentence);
        
    // Takes a PoS tagged sentence as input and outputs the sentence grouped.
    public void groupSentence();
}

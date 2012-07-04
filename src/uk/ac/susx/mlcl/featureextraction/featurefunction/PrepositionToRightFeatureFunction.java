/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.ArrayList;
import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;

/**
 * Represents the feature function which identifies a preposition on the right of a given token 
 * appearing in the same sentence.
 * @author jp242
 */
public class PrepositionToRightFeatureFunction extends AbstractFeatureFunction{
    
    private final String tag;

    public PrepositionToRightFeatureFunction (String tag){
        this.tag = tag;
    }
    
    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        
        Collection<String> features = new ArrayList<String>();
        int idx = index.getSpan().right;
        boolean prepFound = false;
        
        if(idx > 0){
            while(idx < sentence.size() && prepFound == false)
            {
                Token token = sentence.get(idx);
                final StringBuilder sb = new StringBuilder();
                if(token.getAnnotation(Annotations.ChunkTagAnnotation.class).startsWith(tag)){
                    prepFound = true;
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.LeftNounAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.LeftNounAnnotation.class, features, getPrefix());
                }
                idx++;
            }
        }
        return features;
    } 
}

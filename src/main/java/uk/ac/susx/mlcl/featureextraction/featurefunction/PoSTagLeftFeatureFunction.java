/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * Retrieves as a feature the PoS tag to the left of the current token. If the token
 * is a chunk it retrieves the left pos tag from the head of the chunk (taken as right most).
 * @author jp242
 */
public class PoSTagLeftFeatureFunction extends AbstractFeatureFunction{
    
    public String tag;
    
    public PoSTagLeftFeatureFunction(String tag){
        this.tag = tag;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        if(index.getSpan().unitSpan() && index.getSpan().left > 0){
            final StringBuilder sb = new StringBuilder();
            Token token = sentence.get(index.getSpan().left-1);
            CharSequence c = token.getAnnotation(Annotations.PoSAnnotation.class);
            sb.insert(0, c);
            token.setAnnotation(Annotations.PoSTagLeftAnnotation.class, sb.toString());
            token.addAnnotationToCollection(Annotations.PoSTagLeftAnnotation.class, features, getPrefix());
        }
        else{
            if(!index.getSpan().unitSpan()){
                final StringBuilder sb = new StringBuilder();
                for(int i = index.getSpan().right; i >= index.getSpan().left; i--){
                    if(sentence.get(i).getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag) && i > 0){
                        Token token = sentence.get((i-1));
                        CharSequence c = token.getAnnotation(Annotations.PoSAnnotation.class);
                        sb.insert(0, c);
                        token.setAnnotation(Annotations.PoSTagLeftAnnotation.class, sb.toString());
                        token.addAnnotationToCollection(Annotations.PoSTagLeftAnnotation.class, features, getPrefix());
                        break;
                    }
                }
            }
        }
        return features;
    }
}

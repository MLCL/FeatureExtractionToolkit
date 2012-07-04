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
 *
 * @author jp242
 */
public class PoSTagRightFeatureFunction extends AbstractFeatureFunction{
   
    private final String tag;
    
    public PoSTagRightFeatureFunction(String tag){
        this.tag = tag;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        if(index.getSpan().unitSpan() && index.getSpan().right < (sentence.size()-1)){
            final StringBuilder sb = new StringBuilder();
            Token token = sentence.get(index.getSpan().right+1);
            CharSequence c = token.getAnnotation(Annotations.PoSAnnotation.class);
            sb.insert(0, c);
            token.setAnnotation(Annotations.PoSTagLeftAnnotation.class, sb.toString());
            token.addAnnotationToCollection(Annotations.PoSTagLeftAnnotation.class, features, getPrefix());
        }
        else{
            if(!index.getSpan().unitSpan()){
                final StringBuilder sb = new StringBuilder();
                boolean nounFound = false;
                for(int i = index.getSpan().right; i >= index.getSpan().left; i--){
                    if(sentence.get(i).getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag) && i < (sentence.size()-1)){
                        Token token = sentence.get((i+1));
                        CharSequence c = token.getAnnotation(Annotations.PoSAnnotation.class);
                        sb.insert(0, c);
                        token.setAnnotation(Annotations.PoSTagLeftAnnotation.class, sb.toString());
                        token.addAnnotationToCollection(Annotations.PoSTagLeftAnnotation.class, features, getPrefix());
                    }
                }
            }
        }
        return features;
    }
    
}

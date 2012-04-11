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
 * Adds [as a feature] the first proceeding head of a verb phrase chunk which is within the same sentence as a specified token
 * @author jp242
 */
public class PrecedingVerbHeadFeatureFunction extends AbstractFeatureFunction{

    
    /*
     * @param sentence The entire sentence the specified token is contained within
     * @param index The index token specifying the location of the required 
     */
    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();

        int idx = index.getSpan().left;
        final StringBuilder sb = new StringBuilder();
        boolean verbHeadFound = false;
        
        if(idx > 0){
            while(idx > 0 && verbHeadFound == false){
                Token token = sentence.get(idx);
                if(token.getAnnotation(Annotations.ChunkTagAnnotation.class).contains("V")){
                    verbHeadFound = true;
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.LeftVerbHeadAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.LeftVerbHeadAnnotation.class, features, getPrefix());
                }
                idx--;
            }
        }
        return features;
    }
    
}

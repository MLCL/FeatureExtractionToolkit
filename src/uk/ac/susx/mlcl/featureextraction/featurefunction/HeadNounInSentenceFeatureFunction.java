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
 * A feature function to retrieve all head nouns within the same sentence as the entry
 * @author jp242
 */
public class HeadNounInSentenceFeatureFunction extends AbstractFeatureFunction{
    
    private String chunkTag;
    private String nounTag;
    
    public HeadNounInSentenceFeatureFunction(String chunkTag, String nounTag){
        this.chunkTag = chunkTag;
        this.nounTag  = nounTag;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        
        Collection<String> features = new ArrayList<String>();
        
        for(int i = 0; i < sentence.size(); i++){
            if(sentence.get(i).getAnnotation(Annotations.ChunkTagAnnotation.class).startsWith(chunkTag)){
                boolean nounFound  = false;
                StringBuilder sb = new StringBuilder();
                IntSpan chunkSpan =  sentence.get(i).getAnnotation(Annotations.ChunkSpanAnnotation.class);
                int j = chunkSpan.right;
                while(j >= chunkSpan.left && nounFound == false){
                    if(sentence.get(j).getAnnotation(Annotations.PoSAnnotation.class).startsWith(nounTag)){
                        if(index.getSpan().intersectsOrequals(j)){
                            nounFound = true;
                        }
                        else{
                            final CharSequence c = sentence.get(j).getAnnotation(Annotations.TokenAnnotation.class);
                            sb.insert(0,c);
                            sentence.get(i).setAnnotation(Annotations.HeadNounAnnotation.class, sb.toString());
                            sentence.get(i).addAnnotationToCollection(Annotations.HeadNounAnnotation.class, features, getPrefix());
                            nounFound = true;
                        }
                    }
                    i = chunkSpan.right+1;
                    j--;
                }
            }
        }
        return features;
    }
}

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
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * 
 * @author jp242
 */
public class DeterminerLeftFeatureFunction extends AbstractFeatureFunction{
    
    private final String tag;
    
    public DeterminerLeftFeatureFunction(String tag){
        this.tag = tag;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        int idx = index.getSpan().right;
        IntSpan chunkSpan = sentence.get(idx).getAnnotation(Annotations.ChunkSpanAnnotation.class);
        boolean detFound = false;
        
        if(index.getSpan().unitSpan()){
            int i = idx-1;
            while(i >= chunkSpan.left && detFound == false){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.DeterminerLeftAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.DeterminerLeftAnnotation.class, features, getPrefix());
                    detFound = true;
                }
                i--;
            }
        }
        else {
            int i = idx;
            int detCount = 0;
            while(i >= chunkSpan.left && detFound == false){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)  && detCount > 0){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.DeterminerLeftAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.DeterminerLeftAnnotation.class, features, getPrefix());
                    detFound = true;
                }
                else{
                    if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)  && detCount == 0){
                        detCount ++;
                    }
                }
                i--;
            } 
        }
        return features;
    }
    
    
}

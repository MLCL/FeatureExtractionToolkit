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
 * Represents the feature function which identifies nouns on the left of another noun 
 * appearing in the same chunk.
 * @author jp242
 */
public class NounToLeftFeatureFunction extends AbstractFeatureFunction{

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {

        Collection<String> features = new ArrayList<String>();
        
        if(index.getSpan().left == index.getSpan().right){
            int idx = index.getSpan().left;
            IntSpan chunkSpan = sentence.get(idx).getAnnotation(Annotations.ChunkSpanAnnotation.class);
            boolean nounFound = false;
            int i = idx-1;
            while(i >= chunkSpan.left && nounFound == false){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith("NN")){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.LeftNounAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.LeftNounAnnotation.class, features, getPrefix());
                    nounFound = true;
                }
                i--;
            }
        }
//        for(int i = 0; i < idx; i++)
//        {
//            Token token = sentence.get(i);
//            IntSpan chunkSpan = token.getAnnotation(Annotations.ChunkSpanAnnotation.class);
//
//            final StringBuilder sb = new StringBuilder();
//            if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith("NN")
//                    && chunkSpan.intersects(idx)){
//                
//                final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
//                sb.insert(0,c);
//                token.setAnnotation(Annotations.LeftNounAnnotation.class, sb.toString());
//                token.addAnnotationToCollection(Annotations.LeftNounAnnotation.class, features, getPrefix());
//            }
//        }
        return features;
    }
}
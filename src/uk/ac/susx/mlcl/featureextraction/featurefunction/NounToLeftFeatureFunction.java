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
        //getLeftNouns(sentence);
        
        IntSpan span = index.getSpan();
        final List<String> leftNouns = new ArrayList<String>();

        int i = 0;
        while(!sentence.get(i).equals(index))
        {
            final StringBuilder sb = new StringBuilder();
                    Token token = sentence.get(i);
                    if(token.getAnnotation(Annotations.PoSAnnotation.class).contains("NN")
                            && token.getAnnotation(Annotations.ChunkSpanAnnotation.class).equals(sentence.get(i).getAnnotation(Annotations.ChunkSpanAnnotation.class))){
                        final CharSequence c = sentence.get(i).getAnnotation(Annotations.TokenAnnotation.class);
                        sb.insert(0,c);
                        leftNouns.add(sb.toString());
                        
                        //Do entire sentence not indivual entries chunks but all chunks within entire sentence.
                    }
                    sentence.get(i).setAnnotation(Annotations.LeftNounAnnotation.class, leftNouns);
                    
            i++;
        }
        System.err.println(index.toString());
        
        for(int i = 0; i < sentence.size(); i++){
            
            
        }
        
        return features;
    }
    
    
//    /*
//     * Gets all of the nouns left of the current token within the same chunk
//     */
//    public void getLeftNouns(Sentence sentence){
//        for(int i = 0; i < sentence.size(); i++){
//            if(sentence.get(i).getAnnotation(Annotations.PoSAnnotation.class).contains("NN")){
//                final List<String> leftNouns = new ArrayList<String>();
//                for(int j = 0; j < i; j++){
//                    
//                    final StringBuilder sb = new StringBuilder();
//                    Token token = sentence.get(j);
//                    if(token.getAnnotation(Annotations.PoSAnnotation.class).contains("NN")
//                            && token.getAnnotation(Annotations.ChunkSpanAnnotation.class).equals(sentence.get(i).getAnnotation(Annotations.ChunkSpanAnnotation.class))){
//                        final CharSequence c = sentence.get(j).getAnnotation(Annotations.TokenAnnotation.class);
//                        sb.insert(0,c);
//                        leftNouns.add(sb.toString());
//                        
//                        //Do entire sentence not indivual entries chunks but all chunks within entire sentence.
//                    }
//                    sentence.get(j).setAnnotation(Annotations.LeftNounAnnotation.class, leftNouns);
//                }
//            }
//        }
//    }
}
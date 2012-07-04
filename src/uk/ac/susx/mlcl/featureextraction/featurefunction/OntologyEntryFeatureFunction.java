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
public class OntologyEntryFeatureFunction extends AbstractFeatureFunction{
    
    private final String tag;
    private final int depth;
    private final String delimOne;
    private final String delimTwo;
    
    public OntologyEntryFeatureFunction(String tag, int depth, String delimOne, String delimTwo){
        this.tag = tag;
        this.depth = depth;
        this.delimOne = delimOne;
        this.delimTwo = delimTwo;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        int idx = index.getSpan().right;
        IntSpan chunkSpan = sentence.get(idx).getAnnotation(Annotations.ChunkSpanAnnotation.class);
        boolean tagFound = false;
        int i = idx-1;
        while(i >= chunkSpan.left && tagFound == false){
            final StringBuilder sb = new StringBuilder();
            Token token = sentence.get(i);
            if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)){
                final String ontEnt = token.getAnnotation(Annotations.OntologyEntryAnnotation.class);
                if(ontEnt != null){
                    final String[] ontols = ontEnt.split(delimOne);
                    final StringBuilder sb2 = new StringBuilder();
                    if(depth <= 0){
                        token.addAnnotationToCollection(Annotations.OntologyEntryAnnotation.class, features, getPrefix());
                    }
                    else{
                        for(int j = 0; j < ontols.length; j++){
                            int d = 0;
                            int indx = ontols[j].length()-1;
                            while(d < depth && ontols[j].lastIndexOf(delimTwo, indx) > 0){
                                indx = ontols[j].lastIndexOf(delimTwo,indx-1);
                                d++;
                            }
                            sb2.append(ontols[j].substring(indx+1));
                            if(j <= ontols.length-2){
                                sb2.append("&");
                            }
                        }
                        token.setAnnotation(Annotations.OntologyEntryAnnotation.class, sb2.toString());
                        token.addAnnotationToCollection(Annotations.OntologyEntryAnnotation.class, features, getPrefix());
                    }
                    tagFound = true;
                }
            }
            i--;
        }
        return features;
    }
}

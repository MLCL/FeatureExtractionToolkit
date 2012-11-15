/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import com.sun.tools.internal.ws.processor.model.jaxb.RpcLitMember;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;

/**
 *
 * @author jp242
 */
public class DependencyFeatureFunction extends AbstractFeatureFunction{
    
    private String depRel;
    private final boolean dependant;
    
    public DependencyFeatureFunction(String depRel, boolean dependant){
        this.depRel = depRel;
        this.dependant = dependant;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        Token token = sentence.get(index.getSpan().left);
        if(dependant){
            HashMap<String, ArrayList<String>> deps = sentence.get(index.getSpan().left).getAnnotation(Annotations.DependencyListAnnotation.class);
            ArrayList<String> toks = deps.get(depRel);
            if(toks != null){
                for(String tok : toks){
                    token.setAnnotation(Annotations.DependencyAnnotation.class, tok);
                    token.addAnnotationToCollection(Annotations.DependencyAnnotation.class, features, getPrefix());
                }             
            }
        }
        else{
            HashMap<String, ArrayList<String>> deps = sentence.get(index.getSpan().left).getAnnotation(Annotations.DependencyHeadListAnnotation.class);
            ArrayList<String> toks = deps.get(depRel);
            if(toks != null){
                for(String tok : toks){
                    token.setAnnotation(Annotations.DependencyAnnotation.class, tok);
                    token.addAnnotationToCollection(Annotations.DependencyAnnotation.class, features, getPrefix());
                }             
            }
        }
        return features;
    }
    
}

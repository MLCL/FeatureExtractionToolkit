/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.ChunkSpanAnnotation;

/**
 * A Constraint which only allows keys which appear as the head token of a 
 * given pos tag for their element (chunk).
 * @author jp242
 */
public class PoSHeadOnlyKeyConstraint implements FeatureConstraint{
    
    private String tag;

    public PoSHeadOnlyKeyConstraint(String tag){
        this.tag = tag;
    } 
    
    @Override
    public boolean accept(Sentence s, IndexToken<?> cur, int pos) {
        boolean head = true;
        if(cur.getSpan().left == cur.getSpan().right){
            for(int i = (cur.getSpan().left); i < s.get(cur.getSpan().left).getAnnotation(Annotations.ChunkSpanAnnotation.class).right; i++) {
                if(s.get(i).getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)){
                head = false;
                break;
                }
            }
        }
        return head;
    }

}

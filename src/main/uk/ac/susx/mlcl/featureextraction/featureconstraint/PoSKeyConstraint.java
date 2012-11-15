/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;

/**
 * Allows the user to constrain the output feature set to be comprised of entries having a specific 
 * PoS tag. 
 * @author jp242
 */
public class PoSKeyConstraint implements FeatureConstraint{
    
    private final String tag;

    public PoSKeyConstraint(String tag)
    {
        this.tag = tag;
    }
    
    @Override
    public boolean accept(Sentence s, IndexToken<?> cur, int i)
    {
        boolean accept = true;
        if(cur.getSpan().right == cur.getSpan().left){
            accept = s.get(cur.getSpan().left).getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag);
        }
        return accept;
    }
}

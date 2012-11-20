/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author jackpay
 */
public class OwnFeatureConstraint implements FeatureConstraint{

    @Override
    public boolean accept(Sentence s, IndexToken<?> cur, int i) {
        if(cur.getSpan().unitSpan()){
            return i == cur.getSpan().left;
        }
        return false;
    }
    
}

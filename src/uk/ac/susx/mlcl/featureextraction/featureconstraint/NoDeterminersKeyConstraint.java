/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.FeatureConstraint;

/**
 *
 * @author jackpay
 */
public class NoDeterminersKeyConstraint implements FeatureConstraint{

    @Override
    public boolean accept(Sentence s, IndexToken<?> cur, int i) {
        return !s.get(cur.getSpan().left).getAnnotation(Annotations.PoSAnnotation.class).contains("DT");
    }
  
}

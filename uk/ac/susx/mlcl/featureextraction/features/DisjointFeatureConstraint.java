/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author hiam20
 */
public final class DisjointFeatureConstraint implements FeatureConstraint {

    @Override
    public boolean accept(Sentence s, IndexToken<?> currentToken, int index) {
        return !(currentToken.getSpan().intersects(index));
    }
    
}

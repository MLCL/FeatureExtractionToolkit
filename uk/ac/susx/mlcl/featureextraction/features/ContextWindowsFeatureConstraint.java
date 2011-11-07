/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author hiam20
 */
public class ContextWindowsFeatureConstraint implements FeatureConstraint {

    private final IntSpan window;

    public ContextWindowsFeatureConstraint(IntSpan window) {
        this.window = window;
    }

    @Override
    public boolean accept(Sentence sentence, IndexToken<?> currentToken, int i) {
        return currentToken.getSpan().add(window).
                clamp(0, sentence.size() - 1).intersects(i);
    }

}

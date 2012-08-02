/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * A Feature constraint which constrains the number of tokens being taken as 
 * features either side of a given key. 
 * key 
 * @author Simon Wibberley
 */
public class ContextWindowsFeatureConstraint implements FeatureConstraint {

    private final IntSpan window;
    
    /**
     * @param window An IntSpan object describing the size of the window either side
     * of a given key
     */
    public ContextWindowsFeatureConstraint(IntSpan window) {
        this.window = window;
    }

    @Override
    public boolean accept(Sentence sentence, IndexToken<?> currentToken, int i) {
        return currentToken.getSpan().add(window).
                clamp(0, sentence.size() - 1).intersects(i);
    }

}

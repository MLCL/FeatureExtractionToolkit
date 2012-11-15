/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.featureconstraint;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author Simon Wibberley
 */
public final class DisjointFeatureConstraint implements FeatureConstraint {

    @Override
    public boolean accept(Sentence s, IndexToken<?> currentToken, int index) {
        return !(currentToken.getSpan().intersects(index));
    }

}

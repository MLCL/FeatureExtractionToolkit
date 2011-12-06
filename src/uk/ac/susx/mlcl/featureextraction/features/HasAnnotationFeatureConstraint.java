/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;

/**
 *
 * @author Hamish Morgan
 */
public class HasAnnotationFeatureConstraint implements FeatureConstraint {

    private final String annoType;

    public HasAnnotationFeatureConstraint(String annoType) {
        this.annoType = annoType;
    }

    @Override
    public boolean accept(Sentence s, IndexToken<?> cur, int i) {
        return s.get(i).getAnnotation(annoType) != null;
    }

}

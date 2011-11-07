/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.util.Checks;

/**
 * 
 * @author Simon Wibberley
 */
public abstract class AbstractFeatureFunction implements FeatureFunction {

    private static final String DEFAULT_PREFIX = "";

    private String prefix;

    private final List<FeatureConstraint> constraints;

    public AbstractFeatureFunction(String prefix, FeatureConstraint... constraints) {
        Checks.checkNotNull("prefix", prefix);
        Checks.checkNotNull("constraints", constraints);
        this.constraints = new ArrayList<FeatureConstraint>();
        if (constraints.length > 0)
            this.constraints.addAll(Arrays.asList(constraints));
        this.prefix = prefix;
    }

    public AbstractFeatureFunction(FeatureConstraint... constraints) {
        this(DEFAULT_PREFIX, constraints);
    }

    public final String getPrefix() {
        return prefix;
    }

    public final void setPrefix(String prefix) {
        Checks.checkNotNull("prefix", prefix);
        this.prefix = prefix;
    }

    public final void addConstraint(FeatureConstraint constraint) {
        Checks.checkNotNull("constraint", constraint);
        constraints.add(constraint);
    }

    protected final Collection<FeatureConstraint> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    protected <T> Collection<String> extractFeatures(
            Sentence sentence, IndexToken<?> index, Class<? extends Annotation<T>> annotation) {
        List<String> context = new ArrayList<String>();
        for (int i = 0; i < sentence.size(); ++i) {

            Token t = sentence.get(i);
            boolean accept = true;
            for (FeatureConstraint constraint : getConstraints()) {
                if (!constraint.accept(sentence, index, i)) {
                    accept = false;
                    break;
                }
            }
            if (accept) {
                t.addAnnotationToCollection(annotation, context, getPrefix());
            }
        }
        return context;
    }
}
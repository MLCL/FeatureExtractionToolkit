/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.FeatureConstraint;
import uk.ac.susx.mlcl.lib.Checks;

/**
 * Instantiates the general methods needed for FeatureFunctions
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

    /*
     * Basic constructor
     */
    public AbstractFeatureFunction(FeatureConstraint... constraints) {
        this(DEFAULT_PREFIX, constraints);
    }

    public final String getPrefix() {
        return prefix;
    }

    /*
     * Allows the ability to set a prefix to a feature in order to characterise it
     * as a particular feature
     */
    public final void setPrefix(String prefix) {
        Checks.checkNotNull("prefix", prefix);
        this.prefix = prefix;
    }

    /*
     * Adds a constraint 
     */
    public final void addConstraint(FeatureConstraint constraint) {
        Checks.checkNotNull("constraint", constraint);
        constraints.add(constraint);
    }

    /*
     * Gets all constraints
     */
    protected final Collection<FeatureConstraint> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    /* Iterates over each token in a given sentence adding the necessary
     * annotations to the Tokens collections if all constraints are met.
     * 
     * @param Sentence
     * @param index 
     * @param annotation The key which represents the class of annotation 
     */
    protected <T> Collection<String> extractFeatures(
            Sentence sentence, IndexToken<?> index, Class<? extends Annotation<T>> annotation) {
        List<String> context = new ArrayList<String>(); // Collection to be appended to (e.g. feature list)
        for (int i = 0; i < sentence.size(); ++i) {

            Token t = sentence.get(i);
            boolean accept = true;
            for (FeatureConstraint constraint : getConstraints()) {
                if (!constraint.accept(sentence, index, i)) { // 
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
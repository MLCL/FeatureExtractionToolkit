/*
 * Copyright (c) 2010-2013, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
    
    public AbstractFeatureFunction(String prefix, String tag, FeatureConstraint... constraints) {
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
        this(DEFAULT_PREFIX, null, constraints);
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
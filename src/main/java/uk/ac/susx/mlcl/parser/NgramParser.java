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
package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;
import uk.ac.susx.mlcl.featureextraction.ContextWindowStringConverter;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.CharAnnotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations.NgramAnnotation;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.ContextWindowsFeatureConstraint;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.DisjointFeatureConstraint;
import uk.ac.susx.mlcl.featureextraction.featureconstraint.HasAnnotationFeatureConstraint;
import uk.ac.susx.mlcl.featureextraction.featurefactory.FeatureFactory;
import uk.ac.susx.mlcl.featureextraction.featurefunction.NGramFeatureFunction;
import uk.ac.susx.mlcl.util.IntSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * 
 * @author Simon Wibberley
 */
public class NgramParser extends AbstractParser {

    @Override
    protected String newLineDelim() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class BWConfig extends AbstractParserConfig {

        private static final long serialVersionUID = 1L;

        @Parameter(names = {"-nf", "--ngram-features"},
        description = "Use character ngrams as features.", hidden = true)
        private boolean useNgramAsFeature = true;

        @Parameter(names = {"-tf", "--token-features"},
        description = "Use tokens as features.", hidden = true)
        private boolean useTokenAsFeature = true;

        @Parameter(names = {"-nmin"},
        description = "Smallest N for character N-grams.")
        private int nmin = 1;

        @Parameter(names = {"-nmax"},
        description = "Largest N for character N-grams.")
        private int nmax = 5;

        @Parameter(names = {"-cw", "--context-window"},
        converter = ContextWindowStringConverter.class,
        description = "in the form \"-LEFT+RIGHT\" for LEFT tokens to the left and RIGHT tokens to the right")
        private IntSpan contextWindow = new IntSpan(-5, 5);

        public boolean isUseNgramAsFeature() {
            return useNgramAsFeature;
        }

        public boolean isUseTokenAsFeature() {
            return useTokenAsFeature;
        }

        public int getNmin() {
            return nmin;
        }

        public int getNmax() {
            return nmax;
        }

        public IntSpan getContextWindow() {
            return contextWindow;
        }

    }

    private BWConfig config;

    @Override
    protected String getOutPath() {
        String outPath = config.getOutPath();
        if (config.isUseNgramAsFeature()) {
            outPath += "-nf";
        }
        if (config.isUseNgramAsFeature()) {
            outPath += config.getNmin() + "" + config.getNmax();
        }
        if (config.isUseTokenAsFeature()) {
            outPath += "-tf";
        }
        outPath += "-cw" + config.getContextWindow().left + config.getContextWindow().right;

        return outPath;
    }

	@Override
	protected Map<Object, Object> loadPreparsedEntry(String entry) {
		throw new IllegalStateException("Loading pre-parsed text not implemented for this type");
	}

	public static void main(String[] args) {

        NgramParser tp = new NgramParser();

        tp.init(args);

        tp.parse();

    }

    @Override
    public void init(String[] args) {
        config = new BWConfig();
        config.load(args);
//        setSplitter(new NewlineStringSplitter());
    }

    private List<IndexToken<Character>> getKeyNgrams(int len) {

        List<IndexToken<Character>> keys = new ArrayList<IndexToken<Character>>();

        for (int start = 0; start < len; ++start) {

            for (int i = config.getNmin(); i <= config.getNmax(); ++i) {

                int from = start;
                int to = start + i;

                if (from >= 0 && to < len) {
                    keys.add(new IndexToken<Character>(
                            new IntSpan(from, to - 1), CharAnnotation.class));
                }
            }
        }
        return keys;
    }

    @Override
    protected FeatureFactory buildFeatureFactory() {
        FeatureFactory featureFactory = new FeatureFactory();

        NGramFeatureFunction ngff = new NGramFeatureFunction(
                config.getContextWindow(), config.getNmin(), config.getNmax());
        ngff.addConstraint(new HasAnnotationFeatureConstraint(NgramAnnotation.class.getName()));
        ngff.addConstraint(new DisjointFeatureConstraint());
        ngff.addConstraint(new ContextWindowsFeatureConstraint(config.getContextWindow()));

        featureFactory.addFeature("ngramFeature", ngff);
        return featureFactory;
    }

    @Override
    protected List<Sentence> annotate(Map<Object, Object> map) {
	    //todo assumes the map contains a single string as a value
	    String entry = (String) new ArrayList<Object>(map.values()).get(0);
        final Sentence annotated = new Sentence("");

        final char[] chars = entry.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            final Token token = new Token();
            token.setAnnotation(CharAnnotation.class, chars[i]);
            token.setAnnotation(Annotations.IndexAnnotation.class, i);
            annotated.add(token);
        }

        annotated.addAllKey(getKeyNgrams(chars.length));

        applyFeatureFactory(getFeatureFactory(),annotated);
	    List<Sentence> ret = new ArrayList<Sentence>();
	    ret.add(annotated);
	    return ret;
    }

    @Override
    protected AbstractParserConfig config() {
        return config;
    }
    
    
    @Override
    protected void setKeyConstraints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    protected Map<Object, Object> rawTextParse(CharSequence text) throws ModelNotValidException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

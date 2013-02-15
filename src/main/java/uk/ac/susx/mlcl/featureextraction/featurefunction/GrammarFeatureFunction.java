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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.featurefunction;

import java.util.ArrayList;
import java.util.Collection;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotation;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author jp242
 */
public class GrammarFeatureFunction extends AbstractFeatureFunction{
    
    private String tag;
    private int depth;
    private Class<? extends Annotation<?>> anot;
    private boolean inChunk;
    
    public GrammarFeatureFunction(final String tag, final int depth, 
            final Class<? extends Annotation<?>> anot, final boolean inChunk){
        this.tag = tag;
        this.depth = depth;
        this.anot = anot;
        this.inChunk = inChunk;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        int i = (depth >= 0) ? index.getSpan().left : index.getSpan().right;
        final int limit;
        if(inChunk){
            limit = (depth >= 0) ? sentence.get(index.getSpan().right).getAnnotation(Annotations.ChunkSpanAnnotation.class).right :
                    sentence.get(index.getSpan().left).getAnnotation(Annotations.ChunkSpanAnnotation.class).left;
        }
        else{
            limit = (depth >= 0) ? sentence.size()-1 : 0;
        }

        int tagFound = 0;
        i = (depth >= 0) ? i+1: i-1;
        while((i >= limit && tagFound < (depth - (2*depth))
                && depth <= 0) || (i <= limit && tagFound < depth && depth >= 0)){
            final StringBuilder sb = new StringBuilder();
            Token token = sentence.get(i);
            if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag) || token.getAnnotation(Annotations.ChunkTagAnnotation.class).startsWith(tag)){
                final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                sb.insert(0,c);
                token.setAnnotation((Class<? extends Annotation<String>>) anot, sb.toString());
                token.addAnnotationToCollection((Class<? extends Annotation<String>>) anot, features, getPrefix());
                tagFound++;
            }
            i = (depth > 0) ? i+1 : i-1;
        }
        return features;
    }
    
}

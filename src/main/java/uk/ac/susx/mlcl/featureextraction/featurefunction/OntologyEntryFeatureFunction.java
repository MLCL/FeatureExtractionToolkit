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
public class OntologyEntryFeatureFunction extends AbstractFeatureFunction{
    
    private final String tag;
    private final int depth;
    private final String delimOne;
    private final String delimTwo;
    private final boolean inChunk;
    private final int ontolDepth;
    
    public OntologyEntryFeatureFunction(final String tag, final int ontolDepth, 
            final String delimOne, final String delimTwo, final int depth, final boolean inChunk) {
        this.tag = tag;
        this.depth = depth;
        this.ontolDepth = ontolDepth;
        this.delimOne = delimOne;
        this.delimTwo = delimTwo;
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
            Token token = sentence.get(i);
            if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag) || tag == null){
                final String ontEnt = token.getAnnotation(Annotations.OntologyEntryAnnotation.class);
                if(ontEnt != null){
                    final String[] ontols = ontEnt.split(delimOne);
                    final StringBuilder sb2 = new StringBuilder();
                    if(ontolDepth <= 0){
                        token.addAnnotationToCollection(Annotations.OntologyEntryAnnotation.class, features, getPrefix());
                    }
                    else{
                        for(int j = 0; j < ontols.length; j++){
                            int d = 0;
                            int indx = ontols[j].length()-1;
                            while(d < depth && ontols[j].lastIndexOf(delimTwo, indx) > 0){
                                indx = ontols[j].lastIndexOf(delimTwo,indx-1);
                                d++;
                            }
                            sb2.append(ontols[j].substring(indx+1));
                            if(j <= ontols.length-2){
                                sb2.append("&");
                            }
                        }
                        token.setAnnotation(Annotations.OntologyEntryAnnotation.class, sb2.toString());
                        token.addAnnotationToCollection(Annotations.OntologyEntryAnnotation.class, features, getPrefix());
                    }
                    tagFound ++;
                }
            }
            i = (depth > 0) ? i+1 : i-1;
        }
        return features;
    }
}

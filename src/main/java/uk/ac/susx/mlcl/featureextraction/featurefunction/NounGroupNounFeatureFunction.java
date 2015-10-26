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
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author jp242
 */
public class NounGroupNounFeatureFunction extends AbstractFeatureFunction{
    
    private final String tag;

    public NounGroupNounFeatureFunction(String tag){
        this.tag = tag;
    }
    
    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        int idx = index.getSpan().right;
        IntSpan chunkSpan = sentence.get(idx).getAnnotation(Annotations.ChunkSpanAnnotation.class);
        
        if(index.getSpan().unitSpan()){
            int i = idx-1;
            while(i >= chunkSpan.left){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.NounGroupNounAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.NounGroupNounAnnotation.class, features, getPrefix());
                }
                i--;
            }
        }
        else {
            int i = idx;
            int nounCount = 0;
            while(i >= chunkSpan.left){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)  && nounCount > 0){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation(Annotations.NounGroupNounAnnotation.class, sb.toString());
                    token.addAnnotationToCollection(Annotations.NounGroupNounAnnotation.class, features, getPrefix());
                }
                else{
                    if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)  && nounCount == 0){
                        nounCount ++;
                    }
                }
                i--;
            } 
        }
        return features;
    }
    
}

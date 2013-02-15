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
import java.util.List;
import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.Sentence;
import uk.ac.susx.mlcl.featureextraction.Token;
import uk.ac.susx.mlcl.featureextraction.annotations.Annotations;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 * Retrieves as a feature the PoS tag to the left of the current token. If the token
 * is a chunk it retrieves the left pos tag from the head of the chunk (taken as right most).
 * @author jp242
 */
public class PoSTagLeftFeatureFunction extends AbstractFeatureFunction{
    
    public String tag;
    
    public PoSTagLeftFeatureFunction(String tag){
        this.tag = tag;
    }

    @Override
    public Collection<String> extractFeatures(Sentence sentence, IndexToken<?> index) {
        Collection<String> features = new ArrayList<String>();
        if(index.getSpan().unitSpan() && index.getSpan().left > 0){
            final StringBuilder sb = new StringBuilder();
            Token token = sentence.get(index.getSpan().left-1);
            CharSequence c = token.getAnnotation(Annotations.PoSAnnotation.class);
            sb.insert(0, c);
            token.setAnnotation(Annotations.PoSTagLeftAnnotation.class, sb.toString());
            token.addAnnotationToCollection(Annotations.PoSTagLeftAnnotation.class, features, getPrefix());
        }
        else{
            if(!index.getSpan().unitSpan()){
                final StringBuilder sb = new StringBuilder();
                for(int i = index.getSpan().right; i >= index.getSpan().left; i--){
                    if(sentence.get(i).getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag) && i > 0){
                        Token token = sentence.get((i-1));
                        CharSequence c = token.getAnnotation(Annotations.PoSAnnotation.class);
                        sb.insert(0, c);
                        token.setAnnotation(Annotations.PoSTagLeftAnnotation.class, sb.toString());
                        token.addAnnotationToCollection(Annotations.PoSTagLeftAnnotation.class, features, getPrefix());
                        break;
                    }
                }
            }
        }
        return features;
    }
}

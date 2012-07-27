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
        if(index.getSpan().unitSpan()){
            i = (depth >= 0) ? i+1: i-1;
            while((i >= limit && tagFound < (depth - (2*depth))
                    && depth <= 0) || (i <= limit && tagFound < depth && depth >= 0)){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation((Class<? extends Annotation<String>>) anot, sb.toString());
                    token.addAnnotationToCollection((Class<? extends Annotation<String>>) anot, features, getPrefix());
                    tagFound++;
                }
                i = (depth > 0) ? i+1 : i-1;
            }
        }
        else {
            while((i >= limit && tagFound < (depth - (2*depth))
                    && depth <= 0) || (i <= limit && tagFound < depth && depth >= 0)){
                final StringBuilder sb = new StringBuilder();
                Token token = sentence.get(i);
                if(token.getAnnotation(Annotations.PoSAnnotation.class).startsWith(tag)){
                    final CharSequence c = token.getAnnotation(Annotations.TokenAnnotation.class);
                    sb.insert(0,c);
                    token.setAnnotation((Class<? extends Annotation<String>>) anot, sb.toString());
                    token.addAnnotationToCollection((Class<? extends Annotation<String>>) anot, features, getPrefix());
                    tagFound++;
                }
                i = (depth > 0) ? i+1 : i-1;
            } 
        }
        return features;
    }
    
}

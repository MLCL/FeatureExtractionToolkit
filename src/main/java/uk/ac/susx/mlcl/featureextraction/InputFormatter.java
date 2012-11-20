/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction;

/**
 *
 * @author jp242
 */
public interface InputFormatter extends Formatter {
        
    public void processEntry(final String entry, final Sentence annotated);
    
    public void processElement(final String raw, final Sentence annotated,
                                 final int index);
    
}

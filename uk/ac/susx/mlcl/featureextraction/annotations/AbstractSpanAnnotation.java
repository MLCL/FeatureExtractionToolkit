/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.Arrays;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;

/**
 *
 * @author hiam20
 */
public abstract class AbstractSpanAnnotation extends AbstractAnnotation<int[]> {

    @Override
    public void addToCollection(Collection<? super String> list, String prefix)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @Override
    public String toString() {
        return Arrays.toString(getValue());
    }
    
}

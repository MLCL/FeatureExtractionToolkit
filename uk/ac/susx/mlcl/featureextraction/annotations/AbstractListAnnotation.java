/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.Collection;
import java.util.List;
import javax.naming.OperationNotSupportedException;

/**
 *
 * @param <T> 
 * @author hiam20
 */
public abstract class AbstractListAnnotation<T> extends AbstractAnnotation<List<T>> {

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        boolean first = true;
        for (T s : getValue()) {
            if (!first) {
                out.append("\t");
            }
            out.append(s);
            first = false;
        }
        return out.toString();
    }

    @Override
    public void addToCollection(Collection<? super String> list, String prefix) throws OperationNotSupportedException {
        if (prefix.length() == 0)
            for (T c : getValue()) {
                list.add(c.toString());
            }
        else
            for (T c : getValue()) {
                list.add(prefix + c);
            }
    }
    
}


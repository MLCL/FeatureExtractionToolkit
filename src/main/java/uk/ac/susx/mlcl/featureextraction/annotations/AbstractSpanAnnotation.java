/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction.annotations;

import java.util.Collection;
import javax.naming.OperationNotSupportedException;
import uk.ac.susx.mlcl.util.IntSpan;

/**
 *
 * @author Simon Wibberley
 */
public abstract class AbstractSpanAnnotation extends AbstractAnnotation<IntSpan> {

    @Override
    public void addToCollection(Collection<? super String> list, String prefix)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

}

/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.util;

import java.util.List;

/**
 * 
 * @author Simon Wibberley
 */
public interface DocSplitter {

    public List<String> split(CharSequence input);
}

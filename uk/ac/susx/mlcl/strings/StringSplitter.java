/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.strings;

import java.util.List;

/**
 * 
 * @author Simon Wibberley
 */
public interface StringSplitter {

    public List<String> split(CharSequence input);
}

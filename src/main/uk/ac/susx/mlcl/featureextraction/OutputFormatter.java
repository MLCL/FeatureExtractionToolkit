/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

/**
 * 
 * @author Simon Wibberley
 */
public interface OutputFormatter extends Formatter{

    CharSequence getOutput(IndexToken<?> key);
}

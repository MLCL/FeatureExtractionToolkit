/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

/**
 *
 * @author Simon Wibberley
 */
public class NewlineOutputFormatter implements OutputFormatter {

    @Override
    public CharSequence getOutput(IndexToken<?> key) {
        final CharSequence keyStr = key.getKey();
        final StringBuilder out = new StringBuilder();
        for (final CharSequence feature : key.getFeatures()) {
            out.append(keyStr);
            out.append('\n');
            out.append(feature);
            out.append('\n');
        }
        return out;
    }
    
}

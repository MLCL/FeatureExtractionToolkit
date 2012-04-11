/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Simon Wibberley
 */
public class TabOutputFormatter implements OutputFormatter {

    @Override
    public CharSequence getOutput(IndexToken<?> key) {
        StringBuilder out = new StringBuilder();
        List<CharSequence> features = key.getFeatures();
        if(features.size() > 0 && key.getKey().length() > 0){
            out.append(key.getKey());
            for (CharSequence feature : features) {
                out.append('\t');
                out.append(feature);
            }
            out.append('\n');
        }
        return out;
    }
}

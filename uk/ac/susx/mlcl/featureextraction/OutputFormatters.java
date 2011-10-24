/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserverd.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.List;

/**
 * 
 * @author Simon Wibberley
 */
public class OutputFormatters {

    public static Class<? extends OutputFormatter> defaultFormatter = NewlineOutput.class;

    public static class TabOutput implements OutputFormatter {

        @Override
        public String getOutput(IndexToken<?> key) {
            StringBuilder out = new StringBuilder();
            List<String> features = key.getFeature();
            out.append(key.getKey());
            for (String feature : features) {
                out.append("\t");
                out.append(feature);
            }
            out.append("\n");
            return out.toString();
        }
    }

    public static class NewlineOutput implements OutputFormatter {

        @Override
        public String getOutput(IndexToken<?> key) {

            String keyStr = key.getKey();
            StringBuilder out = new StringBuilder();
            List<String> features = key.getFeature();

            for (String feature : features) {
                out.append(keyStr);
                out.append("\n");
                out.append(feature);
                out.append("\n");
            }

            return out.toString();
        }
    }

    private OutputFormatters() {
    }
}

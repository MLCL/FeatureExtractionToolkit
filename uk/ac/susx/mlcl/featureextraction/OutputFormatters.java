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

    public static final Class<? extends OutputFormatter> defaultFormatterClass = NewlineOutput.class;
    public static final OutputFormatter defaultFormatter = new NewlineOutput();

    public static class TabOutput implements OutputFormatter {

        @Override
        public CharSequence getOutput(IndexToken<?> key) {
            StringBuilder out = new StringBuilder();
            List<CharSequence> features = key.getFeature();
            out.append(key.getKey());
            for (CharSequence feature : features) {
                out.append("\t");
                out.append(feature);
            }
            out.append("\n");
            return out;
        }
    }

    public static class NewlineOutput implements OutputFormatter {

        @Override
        public CharSequence getOutput(IndexToken<?> key) {

            final CharSequence keyStr = key.getKey();
            final StringBuilder out = new StringBuilder();

            for (final CharSequence feature : key.getFeature()) {
                out.append(keyStr);
                out.append("\n");
                out.append(feature);
                out.append("\n");
            }

            return out;
        }
    }

    private OutputFormatters() {
    }
}

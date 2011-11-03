/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.featureextraction;

import java.util.List;

/**
 *
 * @author hiam20
 */
public class TabOutputFormatter implements OutputFormatter {

    @Override
    public CharSequence getOutput(IndexToken<?> key) {
        StringBuilder out = new StringBuilder();
        List<CharSequence> features = key.getFeatures();
        out.append(key.getKey());
        for (CharSequence feature : features) {
            out.append('\t');
            out.append(feature);
        }
        out.append('\n');
        return out;
    }
    
}

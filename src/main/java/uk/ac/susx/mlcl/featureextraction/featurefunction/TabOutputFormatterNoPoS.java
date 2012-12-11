package uk.ac.susx.mlcl.featureextraction.featurefunction;

import uk.ac.susx.mlcl.featureextraction.IndexToken;
import uk.ac.susx.mlcl.featureextraction.OutputFormatter;

import java.util.List;

/**
 * Copyright
 * User: mmb28
 * Date: 11/12/2012
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */
public class TabOutputFormatterNoPoS implements OutputFormatter {

	@Override
	/**
	 * Returns a tab separated CharSequence representation of an entry and its features based on
	 * the given key and its features. Strips any extra information appended to the features
	 **/
	public CharSequence getOutput(IndexToken<?> key) {
		StringBuilder out = new StringBuilder();
		List<CharSequence> features = key.getFeatures();
		if (features.size() > 0 && key.getKey().length() > 0) {
			out.append(key.getKey());
			for (CharSequence feature : features) {
				out.append('\t');
				out.append(feature.toString().split("/")[0]);
			}
			out.append('\n');
		}
		return out;
	}
}

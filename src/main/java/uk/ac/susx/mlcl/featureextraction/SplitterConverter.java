package uk.ac.susx.mlcl.featureextraction;

import com.beust.jcommander.IStringConverter;
import uk.ac.susx.mlcl.strings.TextSplitter;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class SplitterConverter implements IStringConverter<TextSplitter> {

	private static final Logger LOG = Logger.getLogger(FormatterConverter.class.getName());

	@Override
	public final TextSplitter convert(final String value) {
		TextSplitter f = null;
		try {
			try {
				f = ((Class<TextSplitter>) Class.forName(value)).newInstance();
			} catch (ClassNotFoundException e) {
				LOG.log(Level.WARNING, "Formatter: {0} not found.", value);
				System.exit(1);
			}
		} catch (InstantiationException e) {
			LOG.log(Level.SEVERE, null, e);
			System.exit(1);
		} catch (IllegalAccessException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		return f;
	}

}


/*
 * Copyright (c) 2011, Sussex University.
 * All rights reserved.
 */
package uk.ac.susx.mlcl.strings;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author hiam20
 */
public final class NewlineStringSplitter implements StringSplitter {

    public NewlineStringSplitter() {
    }

    @Override
    public List<String> split(CharSequence input) {
        return Arrays.asList(Lazy.NL_REGEX.split(input));
    }

    private static final class Lazy {

        public static final Pattern NL_REGEX = Pattern.compile("\\n");

    }
}

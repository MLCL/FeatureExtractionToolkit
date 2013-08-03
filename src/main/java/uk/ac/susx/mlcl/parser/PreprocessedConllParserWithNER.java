package uk.ac.susx.mlcl.parser;

import com.beust.jcommander.Parameter;

/**
 * Copyright
 * User: mmb28
 * Date: 02/08/2013
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class PreprocessedConllParserWithNER extends PreprocessedConllParser {

    public class PreprocessdConllConfig extends StanConfig {
        @Parameter(names = {"-nerpos", "--useNerTagAsPosTag"},
                description = "Name of the dependancy parser model")
        private boolean normalise = false;

        public boolean isNormalise() {
            return normalise;
        }
    }

    private int nerColumn;
    private boolean normalize = false;
    private PreprocessdConllConfig config;

    public PreprocessedConllParserWithNER(String[] args) {
        super(args);
        this.nerColumn = 4;
    }

    @Override
    public void init(String[] args) {
        super.init(args);
        config = new PreprocessdConllConfig();
        config.load(args);
        this.normalize = config.isNormalise();
    }

    @Override
    protected String processSingleToken(String token) {
        String[] components = token.split("\t");
        String word = components[this.wordColumn];
        String lemma = components[this.lemmaColumn];
        String pos = components[this.posColumn];
        String ner = components[this.nerColumn];

        if (this.normalize)
            if (ner.toUpperCase().equals("PERSON") ||
                    ner.toUpperCase().equals("ORGANIZATION") ||
                    ner.toUpperCase().equals("LOCATION") ||
                    ner.toUpperCase().equals("NUMBER")
                    )
                // use the Stanford NER IOB tag as the PoS tag for all named entities
                pos = ner;
        return word + getPosDelim() + lemma + getPosDelim() + pos;
    }

    public static void main(String[] args) {
        PreprocessedConllParser sp = new PreprocessedConllParserWithNER(args);
        sp.init(args);
        long start = System.currentTimeMillis();
        sp.parse();
        System.out.println("Time (ms) = " + (System.currentTimeMillis() - start));
    }
}

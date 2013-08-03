package uk.ac.susx.mlcl.parser;

import org.junit.Before;

import java.util.ArrayList;

/**
 * {@inheritDoc}
 */
public class PreprocessedConllParserTest extends PreprocessedConllParserWithNERTest {

    protected String prefix;

    public PreprocessedConllParserTest() {
        prefix = "test-conll";
    }

    /**
     * Runs a sample input file through the application
     *
     * @throws Exception
     */
    @Before
    @Override
    public void setUp() throws Exception {
        String[] params = {"@" + confFileLocation};
        PreprocessedConllParser.main(params);

        // features of several manually checked tokens
        expectedOutput = new ArrayList<String>();
        expectedOutput.add("procuress/N T:and T:english amod-DEP:english cc-DEP:and conj-DEP:brothel-keeper cop-DEP:be det-DEP:a dobj-HEAD:die punct-DEP:, rcmod-DEP:identify");
        expectedOutput.add("mother/N T:as T:needham nn-HEAD:needham");
        expectedOutput.add("hogarth/N T:'s T:william nn-DEP:william poss-HEAD:series possessive-DEP:'s");
        expectedOutput.add("notorious/J T:be T:in advcl-HEAD:record cop-DEP:be mark-DEP:although nsubj-DEP:needham prep-DEP:at prep-DEP:in");
    }
}

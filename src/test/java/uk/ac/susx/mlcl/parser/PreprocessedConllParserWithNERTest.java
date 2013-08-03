package uk.ac.susx.mlcl.parser;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Runs two simple input files throught the system, end-to-end, and then checks the output file for several
 * manually verified lines. This black-box approach checks if a few uncommon and distinctive words have no missing and
 * no extra features added by this piece of software
 * <p/>
 * User: mmb28
 * Date: 02/08/2013
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class PreprocessedConllParserWithNERTest extends PreprocessedConllParserTest {

    private String confFileLocation2;
    protected File outfile2;

    public PreprocessedConllParserWithNERTest() {
        confFileLocation = "src/test/resources/conf/params-conll-ner.txt";
        confFileLocation2 = "src/test/resources/conf/params-conll-ner-disabled.txt";
        outfile = new File("src/test/resources/output/test-conll-ner-tf-tb-posalldeps.txt");
        outfile2 = new File("src/test/resources/output/test-conll-ner-disabled-tf-tb-posalldeps.txt");
    }

    /**
     * Disables super's setUp
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSingleSentence() throws IOException {
        String[] params = {"@" + confFileLocation};
        PreprocessedConllParserWithNER.main(params);

        // features of several manually checked tokens, with NER normalisation
        this.expectedOutput = new ArrayList<String>();
        this.expectedOutput.add("procuress/N T:and T:english amod-DEP:english cc-DEP:and conj-DEP:brothel-keeper cop-DEP:be det-DEP:a dobj-HEAD:die punct-DEP:, rcmod-DEP:identify");
        this.expectedOutput.add("mother/PERSON T:as T:needham nn-HEAD:needham");
        this.expectedOutput.add("hogarth/PERSON T:'s T:william nn-DEP:william poss-HEAD:series possessive-DEP:'s");
        this.expectedOutput.add("notorious/J T:be T:in advcl-HEAD:record cop-DEP:be mark-DEP:although nsubj-DEP:needham prep-DEP:at prep-DEP:in");

        checkOutputFileContents(this.outfile, this.expectedOutput);
    }

    @Test
    public void testSingleSentenceNERDisabled() throws IOException {
        String[] params = {"@" + confFileLocation2};
        PreprocessedConllParserWithNER.main(params);

        // features of several manually checked tokens, without NER normalisation
        this.expectedOutput = new ArrayList<String>();
        this.expectedOutput.add("procuress/N T:and T:english amod-DEP:english cc-DEP:and conj-DEP:brothel-keeper cop-DEP:be det-DEP:a dobj-HEAD:die punct-DEP:, rcmod-DEP:identify");
        this.expectedOutput.add("mother/N T:as T:needham nn-HEAD:needham");
        this.expectedOutput.add("hogarth/N T:'s T:william nn-DEP:william poss-HEAD:series possessive-DEP:'s");
        this.expectedOutput.add("notorious/J T:be T:in advcl-HEAD:record cop-DEP:be mark-DEP:although nsubj-DEP:needham prep-DEP:at prep-DEP:in");

        checkOutputFileContents(this.outfile2, this.expectedOutput);
    }


}

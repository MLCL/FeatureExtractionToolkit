package uk.ac.susx.mlcl.parser;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertTrue;

/**
 * {@inheritDoc}
 */
public class PreprocessedConllParserTest {

    protected String confFileLocation;
    protected ArrayList<String> expectedOutput;
    protected File outfile;

    public PreprocessedConllParserTest() {
        confFileLocation = "src/test/resources/conf/params-conll.txt";
        outfile = new File("src/test/resources/output/test-conll-tf-tb-posalldeps.txt");
    }

    /**
     * Runs a sample input file through the application
     *
     * @throws Exception
     */
    @Before
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

    @Test
    public void testSingleSentence() throws IOException {
        checkOutputFileContents(this.outfile, this.expectedOutput);
    }

    /**
     * Deletes the temp file created by the test above
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (this.outfile.delete())
            System.out.println("Successfully cleaned up after test");
    }


    public static void checkOutputFileContents(File outfile, ArrayList<String> expectedOutput) throws IOException {
        Set<String> data = new HashSet<String>();
        BufferedReader br = new BufferedReader(new FileReader(outfile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\\t");
            String[] features = ArrayUtils.subarray(tokens, 1, tokens.length);
            Arrays.sort(features);
            final String newLine = tokens[0] + " " + join(features, " ");
            data.add(newLine);
        }
        for (String s : expectedOutput) {
            System.out.println(s);
            assertTrue(data.contains(s));
        }
    }
}

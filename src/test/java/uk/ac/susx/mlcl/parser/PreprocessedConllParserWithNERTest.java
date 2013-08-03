package uk.ac.susx.mlcl.parser;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Runs two simple input files throught the system, end-to-end, and then checks the output file for several
 * manually verified lines. This black-box approach checks if a few uncommon and distinctive words have no missing and
 * no extra features added by this piece of software
 *
 * User: mmb28
 * Date: 02/08/2013
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class PreprocessedConllParserWithNERTest {

    protected String confFileLocation;
    protected String outDir;
    protected static ArrayList<String> expectedOutput;

    private String prefix;

    public PreprocessedConllParserWithNERTest() {
        confFileLocation = "src/test/resources/conf/params-conll-ner.txt";
        outDir = "src/test/resources/output";
        prefix = "test-conll-ner";
    }

    /**
     * Runs a sample input file through the application
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        String[] params = {"@" + confFileLocation};
        PreprocessedConllParserWithNER.main(params);

        // features of several manually checked tokens
        expectedOutput = new ArrayList<String>();
        expectedOutput.add("procuress/N T:and T:english amod-DEP:english cc-DEP:and conj-DEP:brothel-keeper cop-DEP:be det-DEP:a dobj-HEAD:die punct-DEP:, rcmod-DEP:identify");
        expectedOutput.add("mother/PERSON T:as T:needham nn-HEAD:needham");
        expectedOutput.add("hogarth/PERSON T:'s T:william nn-DEP:william poss-HEAD:series possessive-DEP:'s");
        expectedOutput.add("notorious/J T:be T:in advcl-HEAD:record cop-DEP:be mark-DEP:although nsubj-DEP:needham prep-DEP:at prep-DEP:in");
    }

    @Test
    public void testSingleSentence() throws IOException {
        checkOutputFileContents(this.outDir, this.prefix);
    }

    /**
     * Deletes the temp file created by the test above
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        File outfile = getOutputFile(this.outDir, this.prefix);
        if (outfile.delete())
            System.out.println("Successfully cleaned up after test");
    }


    public static void checkOutputFileContents(String outdir, String prefix) throws IOException {
        Set<String> data = new HashSet<String>();

        File outfile = getOutputFile(outdir, prefix);
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

    public static File getOutputFile(String outDir, final String prefix) {
        File outputDirFile = new File(outDir);
        String[] files = outputDirFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(prefix);
            }
        });
        assertEquals("One output file per test please", 1, files.length);
        return new File(outputDirFile.getAbsolutePath() + "/" + files[0]);
    }
}

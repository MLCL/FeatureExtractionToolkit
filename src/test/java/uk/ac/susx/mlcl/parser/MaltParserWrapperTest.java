package uk.ac.susx.mlcl.parser;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: reseter
 * Date: 26/12/12
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class MaltParserWrapperTest {

    final String[] singleProcessedSentFromStanford = {
            "i/i/PRP",
            "solve/solve/VB",
            "the/the/DT",
            "problem/problem/NN",
            "with/with/IN",
            "statistics/statistics/NN",
            "././."};
    final String[] expectedResult = new String[]{
            "1\ti\ti\tPRP\tPRP\t_",
            "2\tsolve\tsolve\tVB\tVB\t_",
            "3\tthe\tthe\tDT\tDT\t_",
            "4\tproblem\tproblem\tNN\tNN\t_",
            "5\twith\twith\tIN\tIN\t_",
            "6\tstatistics\tstatistics\tNN\tNN\t_",
            "7\t.\t.\t.\t.\t_"
    };

    @Test
    public void testFormatSentenceForMaltParser() throws Exception {
        MaltParserWrapper mpw = new MaltParserWrapper("/", "engmalt.linear-1.7.mco",0);
        String[] result = mpw.formatSentenceForMaltParser(singleProcessedSentFromStanford);
        for (int i = 0; i < result.length; i++) {
            assertEquals(expectedResult[i], result[i]);

        }
    }
}

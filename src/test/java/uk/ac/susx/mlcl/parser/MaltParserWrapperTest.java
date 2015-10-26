/*
 * Copyright (c) 2010-2013, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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

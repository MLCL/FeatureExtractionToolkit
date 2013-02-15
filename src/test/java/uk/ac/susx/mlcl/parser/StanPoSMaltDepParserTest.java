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

import org.junit.Before;
import org.junit.Test;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.syntaxgraph.DependencyStructure;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: reseter
 * Date: 26/12/12
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class StanPoSMaltDepParserTest {
    private static final String RESOURCES_DIR = "src/test/resources/";

    //example sentence from the malt parser website
    private static final String[] swedishSent = {"1\tDen\t_\tPO\tPO\tDP\t2\tSS\t_\t_",
            "2\tblir\t_\tV\tBV\tPS\t0\tROOT\t_\t_" ,
            "3\tgemensam\t_\tAJ\tAJ\t_\t2\tSP\t_\t_" ,
            "4\tfÃ¶r\t_\tPR\tPR\t_\t2\tOA\t_\t_" ,
            "5\talla\t_\tPO\tPO\tTP\t6\tDT\t_\t_" +
            "6\tinkomsttagare\t_\tN\tNN\tHS\t4\tPA\t_\t_",
            "7\toavsett\t_\tPR\tPR\t_\t2\tAA\t_\t_",
            "8\tcivilstÃ¥nd\t_\tN\tNN\tSS\t7\tPA\t_\t_" ,
            "9\t.\t_\tP\tIP\t_\t2\tIP\t_\t_"};

    private static StanPoSMaltDepParser maltParserPipeline;


    final String singleRawSentence =  "i/i/PRP solve/solve/VB the/the/DT problem/problem/NN with/with/IN statistics/statistics/NN ././.";
    final String[] singleParsedSentence = new String[]{
            "1\ti\ti\tPRP\tPRP\t_\t2\tnsubj",
            "2\tsolve\tsolve\tVB\tVB\t_\t0\tnull", //todo is this how the root should be marked?
            "3\tthe\tthe\tDT\tDT\t_\t4\tdet", // todo det instead of nmod?
            "4\tproblem\tproblem\tNN\tNN\t_\t2\tdobj",
            "5\twith\twith\tIN\tIN\t_\t2\tprep", //todo vmod or prep?
            "6\tstatistics\tstatistics\tNN\tNN\t_\t5\tpobj", //todo pobj or pmod?
            "7\t.\t.\t.\t.\t_\t2\tpunct"
    };

    @Before
    public void createModels(){
        maltParserPipeline = new StanPoSMaltDepParser();
        assertNull(maltParserPipeline.parsers);
        assertNull(maltParserPipeline.pipelines);
        //test if the stanford and malt workers have been loaded correctly
        String[] args = {"@" + RESOURCES_DIR + "params1.txt"};
        maltParserPipeline.init(args);
        assertNotNull(maltParserPipeline.parsers);
        assertNotNull(maltParserPipeline.pipelines);
        assertEquals(1, maltParserPipeline.parsers.size());
        assertEquals(1, maltParserPipeline.pipelines.size());
    }


    @Test
    public void testMaltPipeline() throws MaltChainedException {
        MaltParserWrapper parser = maltParserPipeline.parsers.remove();

        //test if the dependency parser is functional
        DependencyStructure swedishTree = parser.parse(swedishSent);
        assertEquals(0, maltParserPipeline.parsers.size()); //parser must not be there
        assertNotNull(swedishTree);
        assertTrue(0 < swedishTree.getEdges().size());

        //parse a single sentence
        String[] toParse = singleRawSentence.split(" ");
        String[] parsedString = parser.parseTokens(parser.formatSentenceForMaltParser(toParse));
        DependencyStructure parsedGraph = parser.parse(parser.formatSentenceForMaltParser(toParse));
        //check if the output, formatted as a string, mathes the expected output
        assertArrayEquals(singleParsedSentence, parsedString);
        //check that the output graph matches the output string converted to a graph
        assertEquals(parsedGraph.toString(), parser.toDependencyStructure(parsedString).toString());
    }
}

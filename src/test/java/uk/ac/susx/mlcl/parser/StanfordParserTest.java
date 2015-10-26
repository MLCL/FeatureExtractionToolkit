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
import uk.ac.susx.mlcl.featureextraction.Sentence;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: reseter
 * Date: 26/12/12
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class StanfordParserTest {
    /*example from the TurboParser homepage at
   http://www.ark.cs.cmu.edu/TurboParser/ */
    final String singleRawSent = "I solved the problem with statistics.";
    final String singleProcessedSent =
            "i/i/PRP solve/solve/VBD the/the/DT problem/problem/NN with/with/IN statistics/statistics/NNS ././.";

    final String twoRawSents = "I solved the problem with statistics. People continue to inquire the Reasons for the race for outer space.";
    //must have two sentences, with original tokens replaced by their lemmatized and lowercased forms,
    final String[] twoProcessedSents =
            {"i/i/PRP solve/solve/VBD the/the/DT problem/problem/NN with/with/IN statistics/statistics/NNS ././.",
                    "people/people/NNS continue/continue/VBP to/to/TO inquire/inquire/VB the/the/DT reason/reason/NNS for/for/IN the/the/DT race/race/NN for/for/IN outer/outer/JJ space/space/NN ././."};

    @Test
    public void testSingleSentence() {
        StanfordParser sp = getSingleThreadedStanfordParser();

        Map<Object, Object> sentMap = sp.rawTextParse(singleRawSent);
        assertEquals(1, sentMap.size());
        for (Object o : sentMap.values()) { //only one iteration
            assertTrue(o instanceof String);
            String val = (String) o;
            assertEquals(singleProcessedSent, val);
        }
        System.out.println(sentMap);
    }

    @Test
    public void testMultipleSentences() {
        StanfordParser sp = getSingleThreadedStanfordParser();

        Map<Object, Object> sentMap = sp.rawTextParse(twoRawSents);
        assertEquals(2, sentMap.size());//test sentence segmenter
        Object[] sentences = sentMap.values().toArray();
        for (int i = 0; i < sentences.length; i++) {
            Object o = sentences[i];
            assertTrue(o instanceof String);
            String sentence = (String) o;
            assertEquals(twoProcessedSents[i], sentence);
        }
    }

    private StanfordParser getSingleThreadedStanfordParser() {
        final StanfordParser.StanConfig mockConfig = mock(StanfordParser.StanConfig.class);

        StanfordParser sp = new StanfordParser() {
	        @Override
	        protected Map<Object, Object> loadPreparsedEntry(String entry) {
		        throw new IllegalStateException("Loading pre-parsed text not implemented for this type");
	        }

	        @Override
            protected List<Sentence> annotate(Map<Object, Object> map) {
                return null;
            }

            @Override
            protected StanConfig config() {
                return mockConfig;
            }

            @Override
            public void init(String[] args) {

            }
        };

        when(mockConfig.getNumCores()).thenReturn(1);
        when(mockConfig.isUseLowercaseEntries()).thenReturn(true);
        when(mockConfig.isUseLemma()).thenReturn(true);
        when(mockConfig.isUseCoarsePos()).thenReturn(false);
        sp.initPreProcessor();

        //sanity check
        assertEquals(1, sp.pipelines.size());

        return sp;
    }
}

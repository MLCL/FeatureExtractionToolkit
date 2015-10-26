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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;
import org.maltparser.core.options.OptionManager;
import org.maltparser.core.symbol.SymbolTable;
import org.maltparser.core.syntaxgraph.DependencyStructure;
import org.maltparser.core.syntaxgraph.edge.Edge;

import java.util.ArrayList;

/**
 * A thin wrapper around a MaltParserService that allows multiple instances of the  service to be created
 *
 * @author jackpay
 * @author Miroslav Batchkarov
 */
public class MaltParserWrapper {

  private String posDelim;
  private MaltParserService service;

  private static final String CONLL_DELIM = "\t";
  private static final String DEP_REL_NAME = "DEPREL"; // Name of table containing dep rel strings.
  private static final String TOK_TAB_NAME = "FORM"; // Name of table containing token strings


  public MaltParserWrapper(final String posDelim, final String modName, int id) {
    this.posDelim = posDelim;

    try {
      System.err.println("Initialising the dependency parser model...");
      service = new MaltParserService(id);
      // Inititalize the parser model 'model0' and sets the working directory to '.' and sets the logging file to 'parser.log'
      service.initializeParserModel("-c " + modName + " -m parse -w src/main/resources");
      System.err.println("Model initialised");
    } catch (MaltChainedException e) {
      System.err.println("MaltParser exception: " + e.getMessage());

    }
  }

  public DependencyStructure toDependencyStructure(String[] tokens) throws MaltChainedException {
    DependencyStructure graph = service.toDependencyStructure(tokens);
    return graph;
  }

  public String[] parseTokens(String[] sentence) throws MaltChainedException {
    return service.parseTokens(sentence);
  }

  public DependencyStructure parse(String[] sentence) throws MaltChainedException {
    return service.parse(sentence);
  }

  public String[] formatSentenceForMaltParser(String[] sentence) {
    ArrayList<String> preSent = new ArrayList<String>(sentence.length);

    for (int i = 0; i < sentence.length; i++) {
      String[] tokpos = sentence[i].split(posDelim);
      // TODO: Create input formatter.

      //ignore empty tokens or ones without a PoS tag
      if (sentence[i].length() > 0 && tokpos[0].length() > 0 && tokpos[1].length() > 0) {
        String token = (i + 1) + CONLL_DELIM + tokpos[0] + CONLL_DELIM + tokpos[1] + CONLL_DELIM +
            tokpos[2] + CONLL_DELIM + tokpos[2] + CONLL_DELIM + "_";
        preSent.add(token);
      }
    }
    return preSent.toArray(new String[preSent.size()]);
  }

  public static int getHeadIndex(Edge edge) {
    return edge.getSource().getIndex();
  }

  public static int getDependantIndex(Edge edge) {
    return edge.getTarget().getIndex();
  }

  public static String getDepRel(Edge edge, DependencyStructure graph) throws MaltChainedException {
    return edge.getLabelSymbol(getSymbolTable(DEP_REL_NAME, graph));
  }

  public static String getDependant(Edge edge, DependencyStructure graph) throws MaltChainedException {
    return edge.getTarget().getLabelSymbol(getSymbolTable(TOK_TAB_NAME, graph));
  }

  public static String getHead(Edge edge, DependencyStructure graph) throws MaltChainedException {
    return edge.getSource().getLabelSymbol(getSymbolTable(TOK_TAB_NAME, graph));
  }

  public static SymbolTable getSymbolTable(String table, DependencyStructure graph) throws MaltChainedException {
    return graph.getSymbolTables().getSymbolTable(table);
  }

  @Override
  public String toString() {
    return "MaltParserWrapper{" +
        "posDelim='" + posDelim + '\'' +
        ", service=" + service +
        ", option manager=" + OptionManager.instance() +
        '}';
  }
}

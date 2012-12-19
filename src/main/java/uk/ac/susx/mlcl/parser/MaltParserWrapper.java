/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.parser;

import org.maltparser.MaltParserService;
import org.maltparser.core.exception.MaltChainedException;
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

	public Iterable<Edge> getEdges(DependencyStructure graph) {
		return graph.getEdges();
	}

	public int getHeadIndex(Edge edge) {
		return edge.getSource().getIndex();
	}

	public int getDependantIndex(Edge edge) {
		return edge.getTarget().getIndex();
	}

	public String getDepRel(Edge edge, DependencyStructure graph) throws MaltChainedException {
		return edge.getLabelSymbol(getSymbolTable(DEP_REL_NAME, graph));
	}

	public String getDependant(Edge edge, DependencyStructure graph) throws MaltChainedException {
		return edge.getTarget().getLabelSymbol(getSymbolTable(TOK_TAB_NAME, graph));
	}

	public String getHead(Edge edge, DependencyStructure graph) throws MaltChainedException {
		return edge.getSource().getLabelSymbol(getSymbolTable(TOK_TAB_NAME, graph));
	}

	public SymbolTable getSymbolTable(String table, DependencyStructure graph) throws MaltChainedException {
		return graph.getSymbolTables().getSymbolTable(table);
	}

}

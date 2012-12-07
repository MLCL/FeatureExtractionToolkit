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

import java.io.File;
import java.util.ArrayList;

/**
 * @author jackpay
 */
public class MaltParser implements DependencyParserInterface {

	private String posDelim;
	private String tokDelim;
	private String modName;
	private DependencyStructure currGraph;
	private MaltParserService service;

	private static final String CONLL_DELIM = "\t";
	private static final String DEP_REL_NAME = "DEPREL"; // Name of table containing dep rel strings.
	private static final String TOK_TAB_NAME = "FORM"; // Name of table containing token strings


	public MaltParser(final String posDelim, final String modName) {
		this.posDelim = posDelim;
		this.modName = modName;
	}

	public DependencyStructure parseSentence(final String[] tokens, boolean setGraph) throws MaltChainedException {
		DependencyStructure graph = null;
		;
		for (String tok : tokens) {
			if (tok == "") {
				System.err.println(tokens);
			}
		}
		try {
			graph = service.parse(tokens);

		} catch (MaltChainedException e) {
			for (String tok : tokens) {
				System.err.println(tok);
			}
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getMessageChain());
			System.err.println();
			System.err.println("length: " + tokens.length + "last index: " + tokens[tokens.length - 1]);

		}
		if (setGraph) {
			setGraph(graph);
		}
		return graph;
	}

	public DependencyStructure toDependencyStructure(String[] tokens, boolean setGraph) throws MaltChainedException {
		DependencyStructure graph = service.toDependencyStructure(tokens);
		if (setGraph) {
			currGraph = graph;
		}
		return graph;
	}

	public String[] parseTokens(String[] sentence) throws MaltChainedException {
//		String[] toReturn = null;
//		try {
//			toReturn = service.parseTokens(sentence);
//		} catch (org.maltparser.core.symbol.SymbolException ex) {
//			/* Symbol table error: empty string cannot be added to the symbol table
//			occurs when attempting to parse an empty token */
//		}
//		return toReturn;
		return service.parseTokens(sentence);
	}

	public DependencyStructure getGraph() {
		return currGraph;
	}

	public void setGraph(DependencyStructure graph) {
		currGraph = graph;
	}

	@Override
	public void initialiseModel() {
		try {
			System.err.println("Initialising the dependency parser model...");
			service = new MaltParserService();
			// Inititalize the parser model 'model0' and sets the working directory to '.' and sets the logging file to 'parser.log'
			System.out.println("******** -c " + (new File(".")).getAbsolutePath() + "  src/main/resources/   " + modName);
			service.initializeParserModel("-c " + modName + " -m parse -w src/main/resources" + " -lfi parser.log");
			System.err.println("Model initialised");
		} catch (MaltChainedException e) {
			System.err.println("MaltParser exception: " + e.getMessage());
		}
	}

	public String[] formatSentenceForMaltParser(String[] sentence) {
		ArrayList<String> preSent = new ArrayList<String>(sentence.length);

		for (int i = 0; i < sentence.length; i++) {
			String[] tokpos = sentence[i].split(posDelim);
			// TODO: Create input formatter.

			//ignore empty tokens or ones without a PoS tag
			if (sentence[i].length() > 0 && tokpos[0].length() > 0 && tokpos[1].length() > 0) {
				String token = (i + 1) + CONLL_DELIM + tokpos[0] + CONLL_DELIM + tokpos[1] + CONLL_DELIM + tokpos[2] + CONLL_DELIM + tokpos[2] + CONLL_DELIM + "_";
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

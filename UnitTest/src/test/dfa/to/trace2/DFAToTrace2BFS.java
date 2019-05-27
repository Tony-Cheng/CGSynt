package test.dfa.to.trace2;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import usra.trace2.Statement;
import usra.trace2.Trace;

public class DFAToTrace2BFS {

	public static Trace bfs(NestedWordAutomaton<Statement, String> nwa, Trace initTrace, int length) {
		Set<Node> curNodes = new HashSet<>();
		for (String state : nwa.getInitialStates()) {
			Node next = new Node(state, initTrace);
			curNodes.add(next);
		}
		while (length >= 0) {
			Set<Node> nextNodes = new HashSet<>();
			for (Node node : curNodes) {
				String state = node.state;
				Trace trace = node.trace;
				if (nwa.isFinal(state)) {
					return trace;
				}
				for (Statement statement : nwa.getAlphabet()) {
					for (String next : nwa.succInternal(state, statement)) {
						Trace newTrace = (Trace) trace.mkcpy();
						newTrace.addStatement(statement);
						Node newNode = new Node(next, newTrace);
						nextNodes.add(newNode);
					}
				}
			}
			curNodes = nextNodes;
			length--;
		}
		return null;
	}
}

class Node {
	public final Trace trace;
	public final String state;

	public Node(String state, Trace trace) {
		this.state = state;
		this.trace = trace;
	}
}
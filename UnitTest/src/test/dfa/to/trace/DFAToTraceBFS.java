package test.dfa.to.trace;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import usra.trace.Formula;
import usra.trace.Trace;

public class DFAToTraceBFS {

	public static Set<Trace> bfs(NestedWordAutomaton<Formula, String> nwa, int length) {
		Set<Trace> accepted = new HashSet<>();
		Set<Node> curNodes = new HashSet<>();
		for (String state : nwa.getInitialStates()) {
			Node next = new Node(state, new Trace());
			curNodes.add(next);
		}
		while (length >= 0) {
			Set<Node> nextNodes = new HashSet<>();
			for (Node node : curNodes) {
				String state = node.state;
				Trace trace = node.trace;
				if (nwa.isFinal(state)) {
					accepted.add(trace);
				}
				for (Formula form : nwa.getAlphabet()) {
					for (String next : nwa.succInternal(state, form)) {
						Trace newTrace = (Trace) trace.mkcpy();
						newTrace.addFormulas(form);
						Node newNode = new Node(next, newTrace);
						nextNodes.add(newNode);
					}
				}
			}
			curNodes = nextNodes;
			length--;
		}
		return accepted;
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
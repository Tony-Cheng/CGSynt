package cgsynt.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;

public class CounterexampleConversion<LETTER extends IStatement, STATE> {

	private List<Stack<LETTER>> transitions;
	private boolean resultComputed;
	private List<Stack<IcfgInternalTransition>> resultTransitions;
	private IcfgEdgeFactory edgeFactory;

	public CounterexampleConversion(List<Stack<LETTER>> transitions, IcfgEdgeFactory edgeFactory) {
		this.transitions = transitions;
		this.resultTransitions = new ArrayList<>();
		resultComputed = false;
		this.edgeFactory = edgeFactory;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		resultTransitions = new Stack<>();
		for (int i = 0; i < transitions.size(); i++) {
			resultTransitions.add(computeTransition(transitions.get(i)));
		}
	}

	public List<Stack<IcfgInternalTransition>> getResultTransitions() {
		return resultTransitions;
	}

	private Stack<IcfgInternalTransition> computeTransition(Stack<LETTER> transition) {
		Stack<IcfgInternalTransition> icfgTransition = new Stack<>();
		while (!transition.isEmpty()) {
			icfgTransition
					.push(edgeFactory.createInternalTransition(null, null, null, transition.pop().getTransFormula()));
		}
		Stack<IcfgInternalTransition> invertedTransition = new Stack<>();
		while (!icfgTransition.isEmpty()) {
			invertedTransition.push(icfgTransition.pop());
		}
		return invertedTransition;
	}

}

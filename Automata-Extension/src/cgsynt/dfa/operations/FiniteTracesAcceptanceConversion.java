package cgsynt.dfa.operations;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class FiniteTracesAcceptanceConversion {
	private BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> intersectedAut;
	private BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> result;

	private boolean resultComputed;

	public FiniteTracesAcceptanceConversion(
			BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> intersectedAut) {
		this.intersectedAut = intersectedAut;
		this.resultComputed = false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		result = intersectedAut.mkcpy();
		for (BuchiTreeAutomatonRule<RankedBool, IntersectState<IPredicate, IPredicate>> rule : result.getRules()) {
			if (rule.getSource().getState1().toString().equals("right")) {
				BuchiTreeAutomatonRule<RankedBool, IntersectState<IPredicate, IPredicate>> trueRule = new BuchiTreeAutomatonRule<RankedBool, IntersectState<IPredicate, IPredicate>>(
						RankedBool.TRUE, rule.getSource(), rule.getDest());
				intersectedAut.addRule(trueRule);
			}
		}
		this.resultComputed = true;
	}

	public BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}
}
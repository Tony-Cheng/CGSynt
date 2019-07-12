package cgsynt.nfa;

import java.util.Collection;

import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.minimization.IMinimizationStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class MinimizeStateFactory implements IMinimizationStateFactory<IPredicate> {

	private static int count = 0;

	private BasicPredicateFactory pf;

	public MinimizeStateFactory(BasicPredicateFactory pf) {
		this.pf = pf;
	}

	@Override
	public IPredicate merge(Collection<IPredicate> states) {
		return pf.newDebugPredicate("Simplified: " + count++);
	}

	@Override
	public IPredicate createEmptyStackState() {
		return pf.newDebugPredicate("EmptyState:MinimizeStateFactory");
	}

}

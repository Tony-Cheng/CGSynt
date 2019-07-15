package cgsynt.nfa;

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class GeneralizeStateFactory implements IEmptyStackStateFactory<IPredicate> {

	BasicPredicateFactory pf;

	public GeneralizeStateFactory(BasicPredicateFactory pf) {
		this.pf = pf;
	}

	@Override
	public IPredicate createEmptyStackState() {
		return pf.newDebugPredicate("EmptyStackState:GeneralizeStateFactory");
	}

}

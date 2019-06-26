package cgsynt.nfa;

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class GeneralizeStateFactory implements IEmptyStackStateFactory<IPredicate>{

	@Override
	public IPredicate createEmptyStackState() {
		BasicPredicateFactory pf = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		
		return pf.newDebugPredicate("EmptyStackState:GeneralizeStateFactory");
	}

}

package cgsynt.nfa;

import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IUnionStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class PIUnionStateFactory implements IUnionStateFactory<IPredicate>{
	private static BasicPredicateFactory mPf = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
	
	@Override
	public IPredicate createEmptyStackState() {
		return  mPf.newDebugPredicate("EmptyStackState:PIUnionStateFactory");
	}

	@Override
	public IPredicate createSinkStateContent() {
		return mPf.newDebugPredicate("SinkStatePredicate:PIUnionStateFactory");
	}

	@Override
	public IPredicate union(IPredicate state1, IPredicate state2) {
		return mPf.and(state1, state2);
	}

}

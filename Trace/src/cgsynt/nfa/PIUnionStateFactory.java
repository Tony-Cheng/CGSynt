package cgsynt.nfa;

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IUnionStateFactory;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class PIUnionStateFactory implements IUnionStateFactory<IPredicate>{
	private BasicPredicateFactory mPf;
	private Script script;
	private static BoogieNonOldVar sink = null, empty = null;
	private VariableFactory vf;
	
	public PIUnionStateFactory(Script script, VariableFactory vf, BasicPredicateFactory mPf) {
		this.script = script;
		this.vf = vf;
		this.mPf = mPf;
		
		if (sink == null) {
			try {
				PIUnionStateFactory.sink = vf.constructVariable("sink", VariableFactory.INT);
				PIUnionStateFactory.empty = vf.constructVariable("empty", VariableFactory.INT);
			} catch (Exception e) {
				System.err.println("Could not create sink/empty state placeholders!");
				System.exit(1);
			}
		}
	}
	
	@Override
	public IPredicate createEmptyStackState() {
		return  mPf.newPredicate(script.term("=", empty.getTerm(), script.numeral("0")));
	}

	@Override
	public IPredicate createSinkStateContent() {
		
		return mPf.newPredicate(script.term("=", sink.getTerm(), script.numeral("0")));
	}

	@Override
	public IPredicate union(IPredicate state1, IPredicate state2) {
		return mPf.and(state1, state2);
	}

}

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
	private BasicPredicateFactory mPf = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
	private Script script;
	private static BoogieNonOldVar sink = null, empty = null;
	private VariableFactory vf;
	
	public PIUnionStateFactory() {
		this.script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		this.vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		
		if (sink == null) {
			try {
				PIUnionStateFactory.sink = vf.constructVariable("dontusethisvariablename:sink", VariableFactory.INT);
				PIUnionStateFactory.empty = vf.constructVariable("dontusethisvariablename:empty", VariableFactory.INT);
			} catch (Exception e) {
				System.err.println("Could not create sink/empty state placeholders!");
				System.exit(1);
			}
		}
	}
	
	@Override
	public IPredicate createEmptyStackState() {
		return  mPf.newPredicate(this.script.term("=", empty.getTerm(), script.numeral("0")));
	}

	@Override
	public IPredicate createSinkStateContent() {
		
		return mPf.newPredicate(this.script.term("=", sink.getTerm(), script.numeral("0")));
	}

	@Override
	public IPredicate union(IPredicate state1, IPredicate state2) {
		return mPf.and(state1, state2);
	}

}

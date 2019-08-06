package cgsynt.termination;

import cgsynt.interpol.TraceGlobalVariables;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class OmegaRefiner {
	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOmega;
	private TraceGlobalVariables mGlobalVars;
	
	public OmegaRefiner(TraceGlobalVariables globalVars, NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega) {
		mGlobalVars = globalVars;
		mOmega = omega;
		
		
	}
}

package cgsynt.termination;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class OmegaRefiner {
	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOmega;
	
	public OmegaRefiner(NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega) {
		mOmega = omega;
		
	}
}

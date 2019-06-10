package cgsynt.interpolnfa;

import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class InterpolantToNfa {
	private Set<IPredicate> mInterpolants;
	private Set<NestedWord<IAction>> mTraces;
	
	private NestedWordAutomaton<NestedWord<IAction>, IPredicate> mInterpolantNfa;
	
	public InterpolantToNfa(Set<IPredicate> interpolants, Set<NestedWord<IAction>> traces) {
		this.mInterpolants = interpolants;
		this.mTraces = traces;
		
		this.computeResult();
	}
	
	public NestedWordAutomaton<NestedWord<IAction>, IPredicate> getResult(){
		return mInterpolantNfa;
	}
	
	private void computeResult() {
		IUltimateServiceProvider serviceProvider = UltimateMocks.createUltimateServiceProviderMock();
		
		this.mInterpolantNfa = new NestedWordAutomaton<>();
	}
}

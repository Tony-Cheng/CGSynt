package cgsynt.interpolnfa;

import java.util.Set;

import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class InterpolantToNfa {
	private Set<IPredicate> mInterpolants;
	private Set<IStatement> mTraces;
	private ManagedScript mManagedScript;
	
	private NestedWordAutomaton<IStatement, IPredicate> mInterpolantNfa;
	
	public InterpolantToNfa(Set<IPredicate> interpolants, Set<IStatement> traces, ManagedScript managedScript) {
		this.mInterpolants = interpolants;
		this.mTraces = traces;
		this.mManagedScript = managedScript;
		
		this.computeResult();
	}
	
	public NestedWordAutomaton<IStatement, IPredicate> getResult(){
		return mInterpolantNfa;
	}
	
	private void computeResult() {
		IUltimateServiceProvider serviceProvider = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices automataService = new AutomataLibraryServices(serviceProvider);
		
		VpAlphabet<IStatement> alphabet = new VpAlphabet<>(mTraces);
		
		// This might need to be fixed (the factory argument)
		this.mInterpolantNfa = new NestedWordAutomaton<IStatement, IPredicate>(automataService, alphabet, null);
		
		for (IPredicate pre : this.mInterpolants) {
			for (IStatement statement : this.mTraces) {
				for (IPredicate post : this.mInterpolants) {
					
				}
			}
		}
	}
}

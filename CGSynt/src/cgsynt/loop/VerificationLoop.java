package cgsynt.loop;

import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class VerificationLoop implements ILoop{
	private int mIterationsCap;
	private boolean mStatus;
	
	private IUltimateServiceProvider mServiceProvider;
	private AutomataLibraryServices mAutomataLibraryServices;
	private NestedWordAutomaton<IPredicate, IStatement> mPINfa;
	
	public VerificationLoop(int iterationsCap) {
		this.mIterationsCap = iterationsCap;
		
		this.mServiceProvider = UltimateMocks.createUltimateServiceProviderMock();
		this.mAutomataLibraryServices = new AutomataLibraryServices(this.mServiceProvider);
		
		
	}
	
	@Override
	public void start() {
		for (int i = 0; i < this.mIterationsCap; i++) {
			
		}
	}

	@Override
	public boolean getStatus() {
		return this.mStatus;
	}
	
}

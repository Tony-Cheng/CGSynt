package cgsynt.loop;

import java.util.Set;

import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TraceGeneralization {
	private Set<IPredicate> mInterpolants;
	private Set<IStatement> mTraces;
	private TraceChecker mTraceChecker;
	
	private NestedWordAutomaton<IStatement, IPredicate> mInterpolantNfa;
	
	public TraceGeneralization(Set<IPredicate> interpolants, Set<IStatement> traces, TraceChecker traceChecker) {
		this.mInterpolants = interpolants;
		this.mTraces = traces;
		this.mTraceChecker = traceChecker;
		
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
					boolean unsat = this.mTraceChecker.checkUnsat(pre, statement, post);
					
					if (unsat) {
						Set<IPredicate> states = this.mInterpolantNfa.getStates();
						
						if (!states.contains(pre))
							this.mInterpolantNfa.addState(
									pre.equals(this.mTraceChecker.getTruePredicate()),
									pre.equals(this.mTraceChecker.getFalsePredicate()),
									pre);
						if (!states.contains(post))
							this.mInterpolantNfa.addState(
									post.equals(this.mTraceChecker.getTruePredicate()),
									post.equals(this.mTraceChecker.getFalsePredicate()),
									post);
						
						this.mInterpolantNfa.addInternalTransition(pre, statement, post);
					}
				}
			}
		}
	}
}

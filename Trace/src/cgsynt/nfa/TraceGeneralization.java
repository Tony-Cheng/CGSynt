package cgsynt.nfa;

import java.util.Set;

import cgsynt.interpol.IInterpol;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TraceGeneralization {
	private Set<IPredicate> mInterpolants;
	private Set<IStatement> mTraces;
	private IInterpol mInterpolator;
	
	private NestedWordAutomaton<IStatement, IPredicate> mInterpolantNfa;
	
	public TraceGeneralization(Set<IPredicate> interpolants, Set<IStatement> traces) {
		this.mInterpolants = interpolants;
		this.mTraces = traces;
		this.mInterpolator = TraceToInterpolants.getTraceToInterpolants();
		
		this.mInterpolants.add(TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		this.mInterpolants.add(TraceToInterpolants.getTraceToInterpolants().getFalsePredicate());
		
		for (IPredicate pred : this.mInterpolants) {
			System.out.println(pred.getFormula());
		}
		
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
		this.mInterpolantNfa = new NestedWordAutomaton<IStatement, IPredicate>(automataService, alphabet, new GeneralizeStateFactory<IPredicate>());
		
		for (IPredicate pre : this.mInterpolants) {
			for (IStatement statement : this.mTraces) {
				for (IPredicate post : this.mInterpolants) {
					boolean sat = this.mInterpolator.checkSat(pre, statement, post);
					
					if (!sat) {
						Set<IPredicate> states = this.mInterpolantNfa.getStates();
						
						if (!states.contains(pre))
							this.mInterpolantNfa.addState(
									pre.equals(this.mInterpolator.getTruePredicate()),
									pre.equals(this.mInterpolator.getFalsePredicate()),
									pre);
						if (!states.contains(post))
							this.mInterpolantNfa.addState(
									post.equals(this.mInterpolator.getTruePredicate()),
									post.equals(this.mInterpolator.getFalsePredicate()),
									post);
						
						this.mInterpolantNfa.addInternalTransition(pre, statement, post);
					}
				}
			}
		}
	}
}
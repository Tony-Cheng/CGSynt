package cgsynt.nfa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IInterpol;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TraceGeneralization {
	private Set<IPredicate> mInterpolants;
	private Set<IStatement> mAllStatements;
	private IInterpol mInterpolator;
	
	private List<String> mTransitionsAdded;
	private final boolean DEBUG = false;
	
	private NestedWordAutomaton<IStatement, IPredicate> mInterpolantNfa;
	
	public TraceGeneralization(Set<IPredicate> interpolants, Set<IStatement> allStatements) {
		this.mInterpolants = interpolants;
		this.mAllStatements = allStatements;
		this.mInterpolator = TraceToInterpolants.getTraceToInterpolants();
		
		this.mInterpolants.add(TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		this.mInterpolants.add(TraceToInterpolants.getTraceToInterpolants().getFalsePredicate());
		
		this.mTransitionsAdded = new ArrayList<>();
		
		this.computeResult();
		
		if (DEBUG) {
			System.out.println(this.mTransitionsAdded.size() + " Transitions were created");
			for (String s : this.mTransitionsAdded) {
				System.out.println(s);
			}
		}
	}
	
	public NestedWordAutomaton<IStatement, IPredicate> getResult(){
		return mInterpolantNfa;
	}
	
	private void computeResult() {
		IUltimateServiceProvider serviceProvider = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices automataService = new AutomataLibraryServices(serviceProvider);
		
		VpAlphabet<IStatement> alphabet = new VpAlphabet<>(this.mAllStatements);
		
		// This might need to be fixed (the factory argument)
		this.mInterpolantNfa = new NestedWordAutomaton<IStatement, IPredicate>(automataService, alphabet, new GeneralizeStateFactory());
		
		for (IPredicate pre : this.mInterpolants) {
			for (IStatement statement : this.mAllStatements) {
				for (IPredicate post : this.mInterpolants) {
					boolean unsat = this.mInterpolator.isCorrect(pre, statement, post);
					
					if (unsat) {
						if (DEBUG) {
							String trans = "(" + pre.getFormula().toString() + ", " + statement.toString() + ", " + post.getFormula().toString() + ")";
							this.mTransitionsAdded.add(trans);
						}
						
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
package cgsynt.nfa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IInterpol;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

/**
 * Key things to do when using this class
 * - In the initialization of the currentInterpolants set in the container for this class, 
 * 	 you must add the true and false predicate.
 * - After calculating the interpolants in a given loop iteration, first pass the new and old
 * 	 interpolants to this class, and once the generalization is done, then you can update the 
 *   list of current interpolants with the new ones.
 */
public class OptimizedTraceGeneralization {
	private Set<IPredicate> mCurrentInterpolants;
	private Set<IPredicate> mAdditionalInterpolantSet;
	private Set<IStatement> mAllStatements;
	private IInterpol mInterpolator;
	
	private List<String> mTransitionsAdded;
	private final boolean DEBUG = false;
	
	private NestedWordAutomaton<IStatement, IPredicate> mInterpolantNfa;
	
	public OptimizedTraceGeneralization(
			Set<IPredicate> currentInterpolants, Set<IPredicate> additionalInterpolants,
			Set<IStatement> allStatements, INestedWordAutomaton<IStatement, IPredicate> PI) {
		this.mCurrentInterpolants = currentInterpolants;
		this.mAdditionalInterpolantSet = additionalInterpolants;
		this.mAllStatements = allStatements;
		this.mInterpolantNfa = (NestedWordAutomaton<IStatement, IPredicate>) PI;
				
		this.mInterpolator = TraceToInterpolants.getTraceToInterpolants();
		
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
		addTransitions(this.mCurrentInterpolants, this.mAdditionalInterpolantSet);
		addTransitions(this.mAdditionalInterpolantSet, this.mCurrentInterpolants);
		addTransitions(this.mAdditionalInterpolantSet, this.mAdditionalInterpolantSet);
	}
	
	private void addTransitions(Set<IPredicate> preSet, Set<IPredicate> postSet) {
		for (IPredicate pre : preSet) {
			for (IStatement statement : this.mAllStatements) {
				for (IPredicate post : postSet) {
					boolean unsat = this.mInterpolator.isCorrect(pre, statement, post);
					
					if (unsat) {
						if (DEBUG) {
							String trans = "(" + pre.getFormula().toString() + ", " + statement.toString() + ", " + post.getFormula().toString() + ")";
							this.mTransitionsAdded.add(trans);
						}

						Set<IPredicate> states = this.mInterpolantNfa.getStates();
						
						if (!states.contains(pre))
							this.mInterpolantNfa.addState(false, false, pre);
						if (!states.contains(post))
							this.mInterpolantNfa.addState(false, false, post);
						
						this.mInterpolantNfa.addInternalTransition(pre, statement, post);
					}
				}
			}
		}
	}
}
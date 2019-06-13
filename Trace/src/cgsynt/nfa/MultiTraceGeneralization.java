package cgsynt.nfa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IInterpol;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class MultiTraceGeneralization {
	private List<Set<IPredicate>> mInterpolantSets;
	private List<Set<IStatement>> mTraceSet;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	
	private IInterpol mInterpolator;
	
	private List<String> mTransitionsAdded;
	private final boolean DEBUG = false;
	
	private NestedWordAutomaton<IStatement, IPredicate> mNewPI;
	
	public MultiTraceGeneralization(
			List<Set<IPredicate>> interpolantSets, List<Set<IStatement>> traceSet,
			final NestedWordAutomaton<IStatement, IPredicate> PI) {
		this.mInterpolantSets = interpolantSets;
		this.mTraceSet = traceSet;
		this.mPI = PI;
		
		this.mInterpolator = TraceToInterpolants.getTraceToInterpolants();
	
		// Make sure that the true and false predicates are initially add to pi
		
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
		return mNewPI;
	}
	
	private void computeResult() {
		this.mNewPI = mPI;
		
		// Loop over all the counter examples
		for (int i = 0; i < this.mInterpolantSets.size(); i++) {
			Set<IPredicate> interpolantSet = this.mInterpolantSets.get(i);
			Set<IStatement> trace = this.mTraceSet.get(i);
			
			Set<IPredicate> interpolants = this.mNewPI.getStates();
			interpolants.addAll(interpolantSet);
			
			for (IPredicate pre : interpolants) {
				for (IStatement statement : trace) {
					for (IPredicate post : interpolants) {
						boolean unsat = this.mInterpolator.isCorrect(pre, statement, post);
						
						if (unsat) {
							if (DEBUG) {
								String trans = "(" + pre.getFormula().toString() + ", " + statement.toString() + ", " + post.getFormula().toString() + ")";
								this.mTransitionsAdded.add(trans);
							}
							
							Set<IPredicate> states = this.mNewPI.getStates();
							
							if (!states.contains(pre))
								this.mNewPI.addState(
										pre.equals(this.mInterpolator.getTruePredicate()),
										pre.equals(this.mInterpolator.getFalsePredicate()),
										pre);
							if (!states.contains(post))
								this.mNewPI.addState(
										post.equals(this.mInterpolator.getTruePredicate()),
										post.equals(this.mInterpolator.getFalsePredicate()),
										post);
							
							this.mNewPI.addInternalTransition(pre, statement, post);
						}
					}
				}
			}
		}
	}
}
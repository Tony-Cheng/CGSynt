package cgsynt.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class CounterExamplesToLassoRun<LETTER extends IIcfgTransition<?>, STATE extends IPredicate> {

	private List<Stack<LETTER>> transitions;
	private List<Stack<STATE>> states;
	private List<NestedLassoRun<LETTER, STATE>> result;
	private boolean resultComputed;

	public CounterExamplesToLassoRun(List<Stack<LETTER>> transitions, List<Stack<STATE>> states) {
		this.transitions = transitions;
		this.states = states;
		this.resultComputed = false;
	}

	/**
	 * Compute the counterexamples.
	 */
	public void computeResult() {
		if (resultComputed)
			return;
		result = new ArrayList<>();
		for (int j = 0; j < states.size(); j++) {
			ArrayList<STATE> stemStateSequence = new ArrayList<>();
			ArrayList<LETTER> stemTransitionSequence = new ArrayList<>();
			Stack<STATE> state = states.get(j);
			Stack<LETTER> transition = transitions.get(j);
			stemStateSequence.add(state.pop());
			while (!transition.peek().equals(null)) {
				stemStateSequence.add(state.pop());
				stemTransitionSequence.add(transition.pop());
			}
			transition.pop();
			ArrayList<STATE> loopStateSequence = new ArrayList<>();
			ArrayList<LETTER> loopTransitionSequence = new ArrayList<>();
			loopStateSequence.add(state.pop());
			while (!transition.isEmpty()) {
				loopStateSequence.add(state.pop());
				loopTransitionSequence.add(transition.pop());
			}
			int[] stemNestingRelation = new int[stemTransitionSequence.size()];
			for (int i = 0; i < stemNestingRelation.length; i++) {
				stemNestingRelation[i] = NestedWord.INTERNAL_POSITION;
			}
			int[] loopNestingRelation = new int[loopTransitionSequence.size()];
			for (int i = 0; i < loopNestingRelation.length; i++) {
				loopNestingRelation[i] = NestedWord.INTERNAL_POSITION;
			}
			@SuppressWarnings("unchecked")
			NestedWord<LETTER> stemNestedWord = new NestedWord<>((LETTER[]) stemTransitionSequence.toArray(),
					stemNestingRelation);
			@SuppressWarnings("unchecked")
			NestedWord<LETTER> loopNestedWord = new NestedWord<>((LETTER[]) loopTransitionSequence.toArray(),
					loopNestingRelation);
			NestedRun<LETTER, STATE> stemNestedRun = new NestedRun<>(stemNestedWord, stemStateSequence);
			NestedRun<LETTER, STATE> loopNestedRun = new NestedRun<>(loopNestedWord, loopStateSequence);
			result.add(new NestedLassoRun<>(stemNestedRun, loopNestedRun));
		}
		resultComputed = true;
		return;
	}

	/**
	 * Return a list of counterexamples.
	 * 
	 * @return
	 */
	public List<NestedLassoRun<LETTER, STATE>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

}

package cgsynt.operations;

import java.util.ArrayList;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class CounterExamplesToLassoRun<LETTER extends IIcfgTransition<?>, STATE extends IPredicate> {

	private Stack<LETTER> transitions;
	private Stack<STATE> states;
	private NestedLassoRun<LETTER, STATE> result;
	private boolean resultComputed;

	public CounterExamplesToLassoRun(Stack<LETTER> transitions, Stack<STATE> states) {
		this.transitions = transitions;
		this.states = states;
		this.resultComputed = false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		ArrayList<STATE> stemStateSequence = new ArrayList<>();
		ArrayList<LETTER> stemTransitionSequence = new ArrayList<>();
		stemStateSequence.add(states.pop());
		while (!transitions.peek().equals(null)) {
			stemStateSequence.add(states.pop());
			stemTransitionSequence.add(transitions.pop());
		}
		transitions.pop();
		ArrayList<STATE> loopStateSequence = new ArrayList<>();
		ArrayList<LETTER> loopTransitionSequence = new ArrayList<>();
		loopStateSequence.add(states.pop());
		while (!transitions.isEmpty()) {
			loopStateSequence.add(states.pop());
			loopTransitionSequence.add(transitions.pop());
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
		result = new NestedLassoRun<>(stemNestedRun, loopNestedRun);
		return;
	}

	public NestedLassoRun<LETTER, STATE> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

}

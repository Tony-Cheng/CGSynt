package cgsynt.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cgsynt.nfa.operations.NFACounterexample;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class CounterexampleToLassoRun2<LETTER, STATE> {
	private List<NFACounterexample<LETTER, STATE>> counterexamples;
	private boolean resultComputed;
	private List<NestedLassoRun<LETTER, STATE>> result;

	public CounterexampleToLassoRun2(List<NFACounterexample<LETTER, STATE>> counterexamples) {
		this.counterexamples = counterexamples;
		this.resultComputed = false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		result = new ArrayList<>();
		for (int j = 0; j < counterexamples.size(); j++) {
			ArrayList<STATE> stemStateSequence = new ArrayList<>();
			ArrayList<LETTER> stemTransitionSequence = new ArrayList<>();
			Stack<STATE> stemState = counterexamples.get(j).stemStates;
			Stack<LETTER> stemTransition = counterexamples.get(j).stemTransitions;
			Stack<STATE> loopState = counterexamples.get(j).loopStates;
			Stack<LETTER> loopTransition = counterexamples.get(j).loopTransitions;

			while (!stemState.isEmpty()) {
				stemStateSequence.add(stemState.pop());
			}
			while (!stemTransition.isEmpty()) {
				stemTransitionSequence.add(stemTransition.pop());
			}

			ArrayList<STATE> loopStateSequence = new ArrayList<>();
			ArrayList<LETTER> loopTransitionSequence = new ArrayList<>();
			while (!loopState.isEmpty()) {
				loopStateSequence.add(loopState.pop());
			}
			while (!loopTransition.isEmpty()) {
				loopTransitionSequence.add(loopTransition.pop());
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

	public List<NestedLassoRun<LETTER, STATE>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}
}

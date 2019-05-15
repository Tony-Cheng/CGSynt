import java.util.ArrayList;
import java.util.List;

import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.BuchiTreeAutomatonRule;
import tree.buchi.operations.EmptinessCheck;

public class CheatSheet_EmptynessCheck {

	/**
	 * Create a empty Buchi tree automaton with a single state q0. The initial state
	 * and the final state are both q0. The only transition is ("q0", 'a', "q0",
	 * "q0").
	 * 
	 * @return the automaton described above.
	 */
	public static BuchiTreeAutomaton<TestRankedAlphabet, String> createSimpleNonEmptyAutomaton() {
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut = new BuchiTreeAutomaton<>(2);
		TestRankedAlphabet letter1 = new TestRankedAlphabet('a');

		List<String> list1 = new ArrayList<>();
		list1.add("q0");
		list1.add("q0");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule1 = new BuchiTreeAutomatonRule<>(letter1, "q0", list1);

		aut.addRule(rule1);
		aut.addInitState("q0");
		aut.addFinalState("q0");

		return aut;
	}

	/**
	 * Create a nonempty Buchi tree automaton with two states, q0 and q1. The
	 * initial state and final state are both q0. The transitions are ("q0", 'a',
	 * "q0", "q1") and ("q0", 'b', "q0", "q1").
	 * 
	 * @return
	 */
	public static BuchiTreeAutomaton<TestRankedAlphabet, String> createSimpleEmptyAutomaton() {
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut = new BuchiTreeAutomaton<>(2);
		TestRankedAlphabet letter1 = new TestRankedAlphabet('a');

		List<String> list = new ArrayList<>();
		list.add("q0");
		list.add("q1");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule1 = new BuchiTreeAutomatonRule<>(letter1, "q1", list);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule2 = new BuchiTreeAutomatonRule<>(letter1, "q0", list);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addInitState("q0");
		aut.addFinalState("q0");

		return aut;
	}

	/**
	 * Print true if aut is not empty and false otherwise.
	 * 
	 * @param aut
	 */
	public static void checkAndPrintEmptyness(BuchiTreeAutomaton<TestRankedAlphabet, String> aut) {
		EmptinessCheck<TestRankedAlphabet, String> emptynessChecker = new EmptinessCheck<>(aut);
		System.out.println(emptynessChecker.getResult());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Create a non-empty automaton.
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut1 = createSimpleNonEmptyAutomaton();

		// Check emptiness.
		System.out.println("Is aut1 empty?");
		checkAndPrintEmptyness(aut1);

		// Create an empty automaton.
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut2 = createSimpleEmptyAutomaton();

		// Check emptiness.
		System.out.println("Is aut2 empty?");
		checkAndPrintEmptyness(aut2);
	}
}

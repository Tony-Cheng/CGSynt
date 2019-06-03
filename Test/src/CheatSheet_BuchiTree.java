import java.util.ArrayList;
import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.operations.Intersect;

public class CheatSheet_BuchiTree {

	/**
	 * Create a new BuchiTreeAutomaton. The transitions are ("q0", 'a', "q1", "q2")
	 * and ("q2", 'b', "q3", "q1").
	 * 
	 * @return
	 */
	public static BuchiTreeAutomaton<TestRankedAlphabet, String> createFirstBuchiTree() {
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut1 = new BuchiTreeAutomaton<>(2);
		TestRankedAlphabet letter1 = new TestRankedAlphabet('a');
		TestRankedAlphabet letter2 = new TestRankedAlphabet('b');
		TestRankedAlphabet letter3 = new TestRankedAlphabet('c');
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q3");
		list2.add("q1");
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule1 = new BuchiTreeAutomatonRule<>(letter1, "q0", list1);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule2 = new BuchiTreeAutomatonRule<>(letter2, "q2", list2);
		aut1.addRule(rule1);
		aut1.addRule(rule2);

		return aut1;

	}

	/**
	 * Create a new BuchiTreeAutomaton. The transitions are ("q0", 'a', "q3", "q2")
	 * and ("q3", 'b', "q2", "q1").
	 * 
	 * @return
	 */
	public static BuchiTreeAutomaton<TestRankedAlphabet, String> createSecondBuchiTree() {
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut2 = new BuchiTreeAutomaton<>(2);
		TestRankedAlphabet letter1 = new TestRankedAlphabet('a');
		TestRankedAlphabet letter2 = new TestRankedAlphabet('b');
		TestRankedAlphabet letter3 = new TestRankedAlphabet('c');
		List<String> list3 = new ArrayList<>();
		list3.add("q3");
		list3.add("q2");

		List<String> list4 = new ArrayList<>();
		list4.add("q2");
		list4.add("q1");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule3 = new BuchiTreeAutomatonRule<>(letter1, "q0", list3);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule4 = new BuchiTreeAutomatonRule<>(letter2, "q3", list4);

		aut2.addRule(rule3);
		aut2.addRule(rule4);

		return aut2;

	}

	public static BuchiTreeAutomaton<TestRankedAlphabet, IntersectState<String>> computeIntersect(
			BuchiTreeAutomaton<TestRankedAlphabet, String> aut1, BuchiTreeAutomaton<TestRankedAlphabet, String> aut2) {
		Intersect<TestRankedAlphabet, String> intersect = new Intersect<>(aut1, aut2);
		return intersect.computeResult();

	}

	public static void printAutomaton(BuchiTreeAutomaton aut) {

		System.out.println("Print Transitions");
		for (Object rule : aut.getRules()) {
			System.out.println(rule);
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Create a Buchi tree automaton.
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut1 = createFirstBuchiTree();

		// Create another Buchi tree automaton;
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut2 = createSecondBuchiTree();

		// Intersect two Buchi tree automata.
		BuchiTreeAutomaton<TestRankedAlphabet, IntersectState<String>> intersectAut = computeIntersect(aut1, aut2);

		// Print the transition rules of the first automaton
		System.out.println("Automaton 1");
		printAutomaton(aut1);

		// Print the transition rules of the second automaton
		System.out.println("\nAutomaton 2");
		printAutomaton(aut2);

		// Print the transition rules of the intersected automaton.
		System.out.println("\nIntersect of Automaton 1 and Automaton 2");
		printAutomaton(intersectAut);

	}

}

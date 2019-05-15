import java.util.ArrayList;
import java.util.List;

import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.BuchiTreeAutomatonRule;
import tree.buchi.operations.EmptinessCheck;

public class TestEmptyness {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BuchiTreeAutomaton<TestRankedAlphabet, String> aut1 = new BuchiTreeAutomaton<>(2);

		TestRankedAlphabet letter1 = new TestRankedAlphabet('a');
		TestRankedAlphabet letter2 = new TestRankedAlphabet('b');
		TestRankedAlphabet letter3 = new TestRankedAlphabet('c');

		List<String> list1 = new ArrayList<>();
		list1.add("q0");
		list1.add("q0");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule1 = new BuchiTreeAutomatonRule<>(letter1, "q0", list1);

		aut1.addRule(rule1);
		aut1.addInitState("q0");
		aut1.addFinalState("q0");

		EmptinessCheck<TestRankedAlphabet, String> emptynessChecker = new EmptinessCheck<>(aut1);
		System.out.println(emptynessChecker.getResult());

		BuchiTreeAutomaton<TestRankedAlphabet, String> aut2 = new BuchiTreeAutomaton<>(2);

		List<String> list2 = new ArrayList<>();
		list2.add("q0");
		list2.add("q1");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule2 = new BuchiTreeAutomatonRule<>(letter1, "q1", list2);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule3 = new BuchiTreeAutomatonRule<>(letter1, "q0", list2);

		aut2.addRule(rule3);
		aut2.addRule(rule2);
		aut2.addFinalState("q0");
		aut2.addInitState("q0");

		emptynessChecker = new EmptinessCheck<>(aut2);
		System.out.println(emptynessChecker.getResult());

	}

}

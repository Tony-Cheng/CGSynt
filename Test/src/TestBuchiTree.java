import java.util.ArrayList;
import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.operations.BuchiIntersectTree;

public class TestBuchiTree {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Automaton 1");
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut1 = new BuchiTreeAutomaton<>(2);
		aut1.addState("q0");
		aut1.addState("q1");
		aut1.addState("q2");
		aut1.addState("q3");
		aut1.addInitState("q0");
		aut1.addFinalState("q2");
		aut1.addFinalState("q3");
		TestRankedAlphabet letter1 = new TestRankedAlphabet('a');
		TestRankedAlphabet letter2 = new TestRankedAlphabet('b');
		TestRankedAlphabet letter3 = new TestRankedAlphabet('c');

		aut1.addLetter(letter1);
		aut1.addLetter(letter2);
		aut1.addLetter(letter3);

		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q3");
		list2.add("q1");
		List<String> list6 = new ArrayList<>();
		list6.add("q2");
		list6.add("q3");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule1 = new BuchiTreeAutomatonRule<>(letter1, "q0", list1);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule2 = new BuchiTreeAutomatonRule<>(letter2, "q2", list2);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule6 = new BuchiTreeAutomatonRule<>(letter1, "q0", list6);


		aut1.addRule(rule1);
		aut1.addRule(rule2);
		aut1.addRule(rule6);
		for (String state : aut1.getStates()) {
			System.out.println("State: " + state);
		}
		for (TestRankedAlphabet letter : aut1.getAlphabet()) {
			System.out.println("Letter: " + letter.toString());
		}
		for (String initState : aut1.getInitStates()) {
			System.out.println("Initial State:" + initState);
		}
		for (String finalState : aut1.getFinalStates()) {
			System.out.println("Final State:" + finalState);
		}
		for (BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule : aut1.getRules()) {
			System.out.println("Rule: " + rule.toString());
		}

		System.out.println("\n\nAutomaton 2");
		BuchiTreeAutomaton<TestRankedAlphabet, String> aut2 = new BuchiTreeAutomaton<>(2);
		aut2.addState("q0");
		aut2.addState("q1");
		aut2.addState("q2");
		aut2.addState("q3");
		aut2.addInitState("q0");
		aut2.addFinalState("q1");

		aut2.addLetter(letter1);
		aut2.addLetter(letter2);
		aut2.addLetter(letter3);

		List<String> list3 = new ArrayList<>();
		list3.add("q3");
		list3.add("q2");

		List<String> list4 = new ArrayList<>();
		list4.add("q2");
		list4.add("q1");
		List<String> list5 = new ArrayList<>();
		list5.add("q3");
		list5.add("q1");

		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule3 = new BuchiTreeAutomatonRule<>(letter1, "q0", list3);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule4 = new BuchiTreeAutomatonRule<>(letter2, "q3", list4);
		BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule5 = new BuchiTreeAutomatonRule<>(letter2, "q2", list5);

		
		aut2.addRule(rule3);
		aut2.addRule(rule4);
		aut2.addRule(rule5);

		for (String state : aut2.getStates()) {
			System.out.println("State: " + state);
		}
		for (TestRankedAlphabet letter : aut2.getAlphabet()) {
			System.out.println("Letter: " + letter.toString());
		}
		for (String initState : aut2.getInitStates()) {
			System.out.println("Initial State:" + initState);
		}
		for (String finalState : aut2.getFinalStates()) {
			System.out.println("Final State:" + finalState);
		}
		for (BuchiTreeAutomatonRule<TestRankedAlphabet, String> rule : aut2.getRules()) {
			System.out.println("Rule: " + rule.toString());
		}

		BuchiIntersectTree<TestRankedAlphabet, String> intersect = new BuchiIntersectTree<>(aut1, aut2);
		BuchiTreeAutomaton<TestRankedAlphabet, IntersectState<String>> autResult = intersect.computeResult();

		System.out.println("\n\nInterect Automaton");
		for (IntersectState<String> state : autResult.getStates()) {
			System.out.println("State: " + state);
		}
		for (TestRankedAlphabet letter : autResult.getAlphabet()) {
			System.out.println("Letter: " + letter.toString());
		}
		for (IntersectState<String> initState : autResult.getInitStates()) {
			System.out.println("Initial State:" + initState);
		}
		for (IntersectState<String> finalState : autResult.getFinalStates()) {
			System.out.println("Final State:" + finalState);
		}
		for (BuchiTreeAutomatonRule<TestRankedAlphabet, IntersectState<String>> rule : autResult.getRules()) {
			System.out.println("Rule: " + rule.toString());
		}
	}

}

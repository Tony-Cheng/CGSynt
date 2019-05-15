package test.tree.buchi;

import java.util.ArrayList;
import java.util.List;

import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.BuchiTreeAutomatonRule;

public class BuchiTreeAutomatonFactory {
	public static final RankedLetter LETTERA = new RankedLetter('a', 2);
	public static final RankedLetter LETTERB = new RankedLetter('b', 2);
	public static final RankedLetter LETTERC = new RankedLetter('c', 2);

	/**
	 * Creates a new BuchiTree Automaton that has one state that is both the initial
	 * and final state. It's only transition rule points back to itself along two
	 * edges.
	 */
	public static BuchiTreeAutomaton<RankedLetter, String> single() {
		BuchiTreeAutomaton<RankedLetter, String> machine = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q0");
		list1.add("q0");

		BuchiTreeAutomatonRule<RankedLetter, String> rule = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);

		machine.addRule(rule);
		machine.addInitState("q0");
		machine.addFinalState("q0");

		return machine;
	}

	/**
	 * Create an empty automaton with two states, q0 and q1. q0 is the only initial
	 * and final state. The transitions are (q0, a, q0, q1) and (q1, a, q0, q1).
	 * 
	 * @return the automaton described above
	 */
	public static BuchiTreeAutomaton<RankedLetter, String> doubleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list = new ArrayList<>();
		list.add("q0");
		list.add("q1");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addInitState("q0");
		aut.addFinalState("q1");

		return aut;
	}

	public static BuchiTreeAutomaton<RankedLetter, String> complexNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);

		aut.addInitState("q0");
		aut.addInitState("q4");
		aut.addInitState("q5");
		aut.addFinalState("q1");
		aut.addFinalState("q2");

		List<String> list1 = new ArrayList<>();
		list1.add("q0");
		list1.add("q0");

		List<String> list2 = new ArrayList<>();
		list2.add("q1");
		list2.add("q4");

		List<String> list3 = new ArrayList<>();
		list3.add("q2");
		list3.add("q3");

		List<String> list4 = new ArrayList<>();
		list4.add("q2");
		list4.add("q4");

		List<String> list5 = new ArrayList<>();
		list5.add("q4");
		list5.add("q4");

		List<String> list6 = new ArrayList<>();
		list6.add("q1");
		list6.add("q2");

		List<String> list7 = new ArrayList<>();
		list7.add("q0");
		list7.add("q0");

		List<String> list8 = new ArrayList<>();
		list8.add("q5");
		list8.add("q5");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERB, "q0", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERC, "q1", list3);
		BuchiTreeAutomatonRule<RankedLetter, String> rule4 = new BuchiTreeAutomatonRule<>(LETTERA, "q3", list4);
		BuchiTreeAutomatonRule<RankedLetter, String> rule5 = new BuchiTreeAutomatonRule<>(LETTERA, "q4", list5);
		BuchiTreeAutomatonRule<RankedLetter, String> rule6 = new BuchiTreeAutomatonRule<>(LETTERA, "q4", list6);
		BuchiTreeAutomatonRule<RankedLetter, String> rule7 = new BuchiTreeAutomatonRule<>(LETTERB, "q2", list7);
		BuchiTreeAutomatonRule<RankedLetter, String> rule8 = new BuchiTreeAutomatonRule<>(LETTERC, "q2", list8);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addRule(rule4);
		aut.addRule(rule5);
		aut.addRule(rule6);
		aut.addRule(rule7);
		aut.addRule(rule8);

		return aut;

	}

	public static BuchiTreeAutomaton<RankedLetter, String> complexEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);

		aut.addInitState("q0");
		aut.addInitState("q4");
		aut.addFinalState("q1");

		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q3");
		list2.add("q4");

		List<String> list3 = new ArrayList<>();
		list3.add("q0");
		list3.add("q4");

		List<String> list4 = new ArrayList<>();
		list4.add("q1");
		list4.add("q4");

		List<String> list5 = new ArrayList<>();
		list5.add("q1");
		list5.add("q2");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERB, "q1", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERC, "q2", list3);
		BuchiTreeAutomatonRule<RankedLetter, String> rule4 = new BuchiTreeAutomatonRule<>(LETTERA, "q3", list4);
		BuchiTreeAutomatonRule<RankedLetter, String> rule5 = new BuchiTreeAutomatonRule<>(LETTERB, "q4", list5);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addRule(rule4);
		aut.addRule(rule5);

		return aut;

	}
}

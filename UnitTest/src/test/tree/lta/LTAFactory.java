package test.tree.lta;

import java.util.ArrayList;
import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import test.tree.buchi.RankedLetter;

public class LTAFactory {

	public static final RankedLetter LETTERA = new RankedLetter('a', 2);
	public static final RankedLetter LETTERB = new RankedLetter('b', 2);
	public static final RankedLetter LETTERC = new RankedLetter('c', 2);

	public static BuchiTreeAutomaton<RankedLetter, String> singleNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> machine = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q0");
		list1.add("q0");

		BuchiTreeAutomatonRule<RankedLetter, String> rule = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);

		machine.addRule(rule);
		machine.addInitState("q0");
		machine.setAllStatesFinal();

		return machine;
	}

	public static BuchiTreeAutomaton<RankedLetter, String> singleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> machine = new BuchiTreeAutomaton<>(2);
		machine.addInitState("q0");
		machine.setAllStatesFinal();

		return machine;
	}

	public static BuchiTreeAutomaton<RankedLetter, String> complexNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q4");

		List<String> list2 = new ArrayList<>();
		list2.add("q1");
		list2.add("q2");

		List<String> list3 = new ArrayList<>();
		list3.add("q4");
		list3.add("q3");

		List<String> list4 = new ArrayList<>();
		list4.add("q4");
		list4.add("q3");

		List<String> list5 = new ArrayList<>();
		list5.add("q0");
		list5.add("q1");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERA, "q2", list3);
		BuchiTreeAutomatonRule<RankedLetter, String> rule4 = new BuchiTreeAutomatonRule<>(LETTERA, "q3", list4);
		BuchiTreeAutomatonRule<RankedLetter, String> rule5 = new BuchiTreeAutomatonRule<>(LETTERA, "q4", list5);

		aut.addRules(rule1, rule2, rule3, rule4, rule5);
		aut.addInitState("q0");
		aut.setAllStatesFinal();

		return aut;
	}
	
	public static BuchiTreeAutomaton<RankedLetter, String> complexEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q4");

		List<String> list2 = new ArrayList<>();
		list2.add("q1");
		list2.add("q2");

		List<String> list3 = new ArrayList<>();
		list3.add("q4");
		list3.add("q3");

		List<String> list4 = new ArrayList<>();
		list4.add("q4");
		list4.add("q3");

		List<String> list5 = new ArrayList<>();
		list5.add("q5");
		list5.add("q1");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERA, "q2", list3);
		BuchiTreeAutomatonRule<RankedLetter, String> rule4 = new BuchiTreeAutomatonRule<>(LETTERA, "q3", list4);
		BuchiTreeAutomatonRule<RankedLetter, String> rule5 = new BuchiTreeAutomatonRule<>(LETTERA, "q4", list5);

		aut.addRules(rule1, rule2, rule3, rule4, rule5);
		aut.addInitState("q0");
		aut.setAllStatesFinal();

		return aut;
	}
}

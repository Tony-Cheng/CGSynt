package test.tree.buchi;

import java.util.ArrayList;
import java.util.List;


import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.BuchiTreeAutomatonRule;

public class BuchiTreeAutomatonFactory{
	public static final RankedLetter LETTERA = new RankedLetter('a', 2);
	public static final RankedLetter LETTERB = new RankedLetter('b', 2);
	public static final RankedLetter LETTERC = new RankedLetter('c', 2);
	
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
	
	
}

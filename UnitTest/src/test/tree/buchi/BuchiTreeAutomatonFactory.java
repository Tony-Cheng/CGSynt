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

	public static BuchiTreeAutomaton<RankedLetter, String> LTATripleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q0");
		list2.add("q2");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list2);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addInitState("q0");
		aut.setAllStatesFinal();

		return aut;
	}
	
	public static BuchiTreeAutomaton<RankedLetter, String> SingleMultiLetter() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q0");
		list1.add("q0");
		
		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERB, "q0", list1);
		
		aut.addRule(rule1);
		aut.addRule(rule2);

		return aut;
		
	}

	public static BuchiTreeAutomaton<RankedLetter, String> LTAMultiLetter() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q0");
		list2.add("q2");

		List<String> list3 = new ArrayList<>();
		list3.add("q4");
		list3.add("q3");

		List<String> list4 = new ArrayList<>();
		list4.add("q3");
		list4.add("q0");

		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERB, "q0", list3);
		BuchiTreeAutomatonRule<RankedLetter, String> rule4 = new BuchiTreeAutomatonRule<>(LETTERB, "q3", list4);

		aut.addRules(rule1, rule2, rule3, rule4);

		aut.addInitState("q0");
		aut.setAllStatesFinal();

		return aut;

	}

	public static BuchiTreeAutomaton<RankedLetter, String> EmptyTreeLTA() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q3");
		list2.add("q1");

		List<String> list3 = new ArrayList<>();
		list3.add("q2");
		list3.add("q4");


		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERA, "q2", list3);

		aut.addRules(rule1, rule2, rule3);

		aut.addInitState("q0");
		aut.setAllStatesFinal();

		return aut;
	}
	
	public static BuchiTreeAutomaton<RankedLetter, String> complexEmptyLTA() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);
		List<String> list1 = new ArrayList<>();
		list1.add("q1");
		list1.add("q2");

		List<String> list2 = new ArrayList<>();
		list2.add("q2");
		list2.add("q0");

		List<String> list3 = new ArrayList<>();
		list3.add("q4");
		list3.add("q3");
		
		List<String> list4 = new ArrayList<>();
		list4.add("q0");
		list4.add("q4");
		
		List<String> list5 = new ArrayList<>();
		list5.add("q5");
		list5.add("q2");


		BuchiTreeAutomatonRule<RankedLetter, String> rule1 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", list1);
		BuchiTreeAutomatonRule<RankedLetter, String> rule2 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", list2);
		BuchiTreeAutomatonRule<RankedLetter, String> rule3 = new BuchiTreeAutomatonRule<>(LETTERA, "q2", list3);
		BuchiTreeAutomatonRule<RankedLetter, String> rule4 = new BuchiTreeAutomatonRule<>(LETTERA, "q3", list4);
		BuchiTreeAutomatonRule<RankedLetter, String> rule5 = new BuchiTreeAutomatonRule<>(LETTERA, "q4", list5);


		aut.addRules(rule1, rule2, rule3, rule4, rule5);

		aut.addInitState("q0");
		aut.addInitState("q3");
		aut.setAllStatesFinal();

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

	public static BuchiTreeAutomaton<RankedLetter, String> orderNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> machine = new BuchiTreeAutomaton<>(2);

		machine.addInitState("qz");
		machine.addFinalState("q0");
		machine.addFinalState("q1");
		machine.addFinalState("q2");

		List<String> tz = destList("qx", "q0");
		List<String> ty = destList("qx", "qx");
		List<String> tx1 = destList("qy", "q2");
		List<String> tx2 = destList("q0", "q2");

		List<String> t0 = destList("q1", "q2");
		List<String> t1 = destList("q0", "q2");
		List<String> t2 = destList("q0", "q1");

		BuchiTreeAutomatonRule<RankedLetter, String> rz = new BuchiTreeAutomatonRule<>(LETTERA, "qz", tz);
		BuchiTreeAutomatonRule<RankedLetter, String> ry = new BuchiTreeAutomatonRule<>(LETTERB, "qy", ty);
		BuchiTreeAutomatonRule<RankedLetter, String> rx1 = new BuchiTreeAutomatonRule<>(LETTERC, "qx", tx1);
		BuchiTreeAutomatonRule<RankedLetter, String> rx2 = new BuchiTreeAutomatonRule<>(LETTERA, "qx", tx2);

		BuchiTreeAutomatonRule<RankedLetter, String> r0 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", t0);
		BuchiTreeAutomatonRule<RankedLetter, String> r1 = new BuchiTreeAutomatonRule<>(LETTERB, "q1", t1);
		BuchiTreeAutomatonRule<RankedLetter, String> r2 = new BuchiTreeAutomatonRule<>(LETTERC, "q2", t2);

		addRules(machine, rz, rx1, rx2, ry, r0, r1, r2);

		return machine;
	}

	public static BuchiTreeAutomaton<RankedLetter, String> emptyBuchiTree() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);

		aut.addInitState("q0");
		aut.addFinalState("q4");
		aut.addFinalState("q5");

		List<String> t0 = destList("q1", "q3");
		List<String> t1 = destList("q2", "q3");
		List<String> t2 = destList("q4", "q5");
		List<String> t3 = destList("q6", "q7");

		BuchiTreeAutomatonRule<RankedLetter, String> r0 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", t0);
		BuchiTreeAutomatonRule<RankedLetter, String> r1 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", t1);
		BuchiTreeAutomatonRule<RankedLetter, String> r2 = new BuchiTreeAutomatonRule<>(LETTERA, "q2", t2);
		BuchiTreeAutomatonRule<RankedLetter, String> r3 = new BuchiTreeAutomatonRule<>(LETTERA, "q3", t3);

		addRules(aut, r0, r1, r2, r3);

		return aut;

	}

	public static BuchiTreeAutomaton<RankedLetter, String> nonEmptyBuchiTree() {
		BuchiTreeAutomaton<RankedLetter, String> aut = new BuchiTreeAutomaton<>(2);

		aut.addInitState("q0");
		aut.addFinalState("q4");
		aut.addFinalState("q5");

		List<String> t0 = destList("q1", "q3");
		List<String> t1 = destList("q2", "q3");
		List<String> t2 = destList("q4", "q5");
		List<String> t3 = destList("q6", "q7");
		List<String> t4 = destList("q0", "q3");
		List<String> t5 = destList("q2", "q1");
		List<String> t6 = destList("q4", "q2");
		List<String> t7 = destList("q5", "q5");
		List<String> t8 = destList("q0", "q0");

		BuchiTreeAutomatonRule<RankedLetter, String> r0 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", t0);
		BuchiTreeAutomatonRule<RankedLetter, String> r1 = new BuchiTreeAutomatonRule<>(LETTERA, "q1", t1);
		BuchiTreeAutomatonRule<RankedLetter, String> r2 = new BuchiTreeAutomatonRule<>(LETTERB, "q2", t2);
		BuchiTreeAutomatonRule<RankedLetter, String> r3 = new BuchiTreeAutomatonRule<>(LETTERC, "q3", t3);
		BuchiTreeAutomatonRule<RankedLetter, String> r4 = new BuchiTreeAutomatonRule<>(LETTERA, "q4", t4);
		BuchiTreeAutomatonRule<RankedLetter, String> r5 = new BuchiTreeAutomatonRule<>(LETTERC, "q5", t5);
		BuchiTreeAutomatonRule<RankedLetter, String> r6 = new BuchiTreeAutomatonRule<>(LETTERB, "q6", t6);
		BuchiTreeAutomatonRule<RankedLetter, String> r7 = new BuchiTreeAutomatonRule<>(LETTERA, "q7", t7);
		BuchiTreeAutomatonRule<RankedLetter, String> r8 = new BuchiTreeAutomatonRule<>(LETTERA, "q0", t8);

		addRules(aut, r0, r1, r2, r3, r4, r5, r6, r7, r8);

		return aut;

	}

	public static BuchiTreeAutomaton<RankedLetter, String> parameterizedEmpty(int n) {
		BuchiTreeAutomaton<RankedLetter, String> machine = new BuchiTreeAutomaton<>(2);

		assert n != 0;
		machine.addInitState("q0");
		for (int i = 0; i < (int) Math.pow(2, n) - 1; i++) {
			machine.addFinalState("q" + i);
		}

		for (int i = 0; i < (int) Math.pow(2, n - 1); i++) {
			List<String> t = destList("q" + ((i * 2) + 1), "q" + ((i * 2) + 2));
			BuchiTreeAutomatonRule<RankedLetter, String> rule = new BuchiTreeAutomatonRule<>(LETTERA, "q" + i, t);
			machine.addRule(rule);
		}

		return machine;
	}

	public static BuchiTreeAutomaton<RankedLetter, String> parameterizedNonEmpty(int n) {
		BuchiTreeAutomaton<RankedLetter, String> machine = parameterizedEmpty(n);

		for (int i = (int) Math.pow(2, n - 1) - 1; i < (int) Math.pow(2, n) - 1; i++) {
			List<String> t = destList("q0", "q0");
			BuchiTreeAutomatonRule<RankedLetter, String> rule = new BuchiTreeAutomatonRule<>(LETTERA, "q" + i, t);
			machine.addRule(rule);
		}

		return machine;
	}

	public static BuchiTreeAutomaton<RankedLetter, String> parameterizedNonEmptyWithRandom(int n, int m) {
		BuchiTreeAutomaton<RankedLetter, String> machine = parameterizedNonEmpty(10);

		int numNodes = (int) Math.pow(2, n);

		for (int i = 0; i < m; i++) {
			int s = (int) (Math.random() * numNodes);

			int d1 = (int) (Math.random() * numNodes);
			int d2 = (int) (Math.random() * numNodes);

			List<String> t = destList("q" + d1, "q" + d2);
			BuchiTreeAutomatonRule<RankedLetter, String> rule = new BuchiTreeAutomatonRule<>(LETTERA, "q" + s, t);
			machine.addRule(rule);
		}

		return machine;
	}

	/**
	 * Create a destination ArrayList based on the input strings.
	 * 
	 * @param dests
	 *            The array of Destination Strings
	 * @return destinations The ArrayList of Destinations.
	 */
	private static ArrayList<String> destList(String... dests) {
		ArrayList<String> destination = new ArrayList<>();

		for (String dest : dests) {
			destination.add(dest);
		}

		return destination;
	}

	/**
	 * Add the specified rules to the BTA.
	 * 
	 * @param bta
	 *            The BTA that the rules should be added to.
	 * @param automatonRules
	 *            The array of BTA rules that are to be added.
	 */
	@SafeVarargs
	private static void addRules(BuchiTreeAutomaton<RankedLetter, String> bta,
			BuchiTreeAutomatonRule<RankedLetter, String>... automatonRules) {
		for (BuchiTreeAutomatonRule<RankedLetter, String> rule : automatonRules) {
			bta.addRule(rule);
		}
	}
}

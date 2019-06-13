import java.util.HashSet;
import java.util.Set;

import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestDfaToLta {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BuchiTreeAutomaton testDFAToLTA(NestedWordAutomaton dfa) {
		DfaToLtaPowerSet op = new DfaToLtaPowerSet(dfa);

		return op.getResult();
	}

	public static void main(String[] args) {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<>();
		letters.add('a');
		letters.add('b');
		letters.add('c');

		VpAlphabet<Character> alphabet = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> dfa = new NestedWordAutomaton<>(service, alphabet, new StringFactory());

		dfa.addState(true, false, "q0");
		dfa.addState(false, false, "q1");
		dfa.addState(false, true, "q2");
		dfa.addState(false, true, "q3");
		dfa.addState(false, false, "q4");

		dfa.addInternalTransition("q0", 'a', "q1");
		dfa.addInternalTransition("q0", 'b', "q3");

		dfa.addInternalTransition("q1", 'a', "q0");
		dfa.addInternalTransition("q1", 'b', "q2");

		dfa.addInternalTransition("q2", 'a', "q1");
		dfa.addInternalTransition("q2", 'b', "q4");

		dfa.addInternalTransition("q3", 'a', "q1");
		dfa.addInternalTransition("q3", 'b', "q4");

		dfa.addInternalTransition("q4", 'a', "q3");
		dfa.addInternalTransition("q4", 'b', "q1");

		@SuppressWarnings("unchecked")
		BuchiTreeAutomaton<RankedBool, String> lta = testDFAToLTA(dfa);

		printBuchiTreeAutomaton(lta);
	}

	public static void printBuchiTreeAutomaton(BuchiTreeAutomaton machine) {
		Set<String> initialStates = machine.getInitStates();
		for (String state : initialStates)
			System.out.println("Initial State: " + state);

		System.out.println();

		Set<BuchiTreeAutomatonRule<RankedBool, String>> rules = machine.getRules();
		for (BuchiTreeAutomatonRule<RankedBool, String> rule : rules)
			System.out.println("Rule: " + rule);

		System.out.println();

		Set<String> finalStates = machine.getFinalStates();
		for (String state : finalStates)
			System.out.println("Final State: " + state);
	}
}

import de.uni_freiburg.informatik.ultimate.automata.nestedword.*;
import de.uni_freiburg.informatik.ultimate.test.mocks.*;

import java.util.HashSet;
import java.util.Set;

import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.Word;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Accepts;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;

public class CheatSheet {

	/**
	 * Create and return a DFA.
	 * 
	 * The states are "q0", "q1", and "q2".
	 * 
	 * The letters (aka the alphabet) are 'a', 'b', and 'c'.
	 * 
	 * The transition functions are ("q0",'a', "q1"), ("q1", 'b', "q1"), and ("q1",
	 * 'c', "q2").
	 * 
	 * The initial state is "q0".
	 * 
	 * The final state is "q2".
	 * 
	 * @param service
	 * @return the DFA
	 */
	public static NestedWordAutomaton<Character, String> constructDFA(AutomataLibraryServices service) {

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new GeneralizeStateFactory<>());

		nwa.addState(true, false, "q0");
		nwa.addState(false, false, "q1");
		nwa.addState(false, true, "q2");
		nwa.addInternalTransition("q0", 'a', "q1");
		nwa.addInternalTransition("q1", 'b', "q1");
		nwa.addInternalTransition("q1", 'c', "q2");

		return nwa;

	}

	/**
	 * Check if the word "abbc" and the word "baaab" are accepted by the automaton.
	 * 
	 * @param automaton
	 * @param service
	 */
	public static void useAccepts(INestedWordAutomaton<Character, String> automaton, AutomataLibraryServices service) {
		NestedWord<Character> word1 = NestedWord.nestedWord(new Word<>('a', 'b', 'b', 'c'));
		NestedWord<Character> word2 = NestedWord.nestedWord(new Word<>('b', 'a', 'a', 'a', 'b'));

		try {
			Accepts<Character, String> accept1 = new Accepts<>(service, automaton, word1);
			Accepts<Character, String> accept2 = new Accepts<>(service, automaton, word2);

			System.out.println(word1.toString() + " is accepted? " + accept1.getResult());
			System.out.println(word2.toString() + " is accepted? " + accept2.getResult());

		} catch (AutomataLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create and return a NFA.
	 * 
	 * The states are "q0", "q1", and "q2".
	 * 
	 * The letters (aka the alphabet) are 'a', 'b', and 'c'.
	 * 
	 * The transition functions are ("q0",'a', "q0"), ("q0", 'a', "q1"), ("q1", 'b',
	 * "q2"), ("q0",'b', "q0"), and ("q1",'a', "q2").
	 * 
	 * The initial state is "q0".
	 * 
	 * The final state is "q2".
	 * 
	 * @param service
	 * @return the NFA
	 */
	public static NestedWordAutomaton<Character, String> constructNFA(AutomataLibraryServices service) {

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, false, "q1");
		nwa.addState(false, true, "q2");
		nwa.addInternalTransition("q0", 'a', "q0");
		nwa.addInternalTransition("q0", 'a', "q1");
		nwa.addInternalTransition("q1", 'b', "q2");
		nwa.addInternalTransition("q0", 'b', "q0");
		nwa.addInternalTransition("q1", 'a', "q2");

		return nwa;

	}

	/**
	 * Determinize a NFA.
	 * 
	 * @param service
	 * @param nfa
	 * @return the determinized NFA.
	 */
	public static INestedWordAutomaton<Character, String> determinizeNFA(AutomataLibraryServices service,
			NestedWordAutomaton<Character, String> nfa) {
		try {
			Determinize<Character, String> determinize = new Determinize<>(service,
					(IDeterminizeStateFactory<String>) nfa.getStateFactory(), nfa);
			return determinize.getResult();
		} catch (AutomataOperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Initialization.
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		// How to create a DFA.
		NestedWordAutomaton<Character, String> dfa = constructDFA(service);

		// How to check if a word is accepted or rejected by the DFA.
		useAccepts(dfa, service);

		// How to create a NFA.
		NestedWordAutomaton<Character, String> nfa = constructNFA(service);

		// How to determinized a NFA.
		INestedWordAutomaton<Character, String> determinizedNFA = determinizeNFA(service, nfa);

		// How to check if a word is accepted or rejected by the determinized NFA.
		useAccepts(determinizedNFA, service);
	}

}
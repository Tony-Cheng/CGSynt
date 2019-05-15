package test.fsa;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;

/**
 * Factory class for creating FSAs that can be used for testing.
 *
 */
public class FsaFactory {
	/**
	 * Creates a five state DFA, that has two final states (q2, q3), 
	 * and all letters lead each state to another non-dead state.
	 * 
	 * @param service The service used by the Automaton Library.
	 * 
	 * @return dfa
	 * 		A NestedWordAutomaton that represents a DFA.
	 */
	public static NestedWordAutomaton<Character, String> fiveStateDFA(AutomataLibraryServices service){
		VpAlphabet<Character> alpha = alphabet('a', 'b');
		
		NestedWordAutomaton<Character, String> dfa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
		
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
		
		return dfa;
	}
	
	/**
	 * Initialize a new VpAlphabet with the specified letters.
	 * 
	 * @param chars The characters that are part of this alphabet.
	 * 
	 * @return A new VpAlphabet.
	 */
	private static VpAlphabet<Character> alphabet(char... chars){
		Set<Character> letterSet = new HashSet<>();
		
		for (char c : chars) {
			letterSet.add(c);
		}
		
		return new VpAlphabet<Character>(letterSet);
	}
}

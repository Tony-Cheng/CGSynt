package test.fsa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
		
		addTransitionForAll(dfa, alpha, "q0", "q1", "q3");
		addTransitionForAll(dfa, alpha, "q1", "q0", "q2");
		addTransitionForAll(dfa, alpha, "q2", "q1", "q4");
		addTransitionForAll(dfa, alpha, "q3", "q1", "q4");
		addTransitionForAll(dfa, alpha, "q4", "q3", "q1");
		
		return dfa;
	}
	
	/**
	 * Creates an empty DFA.
	 * 
	 * @param service The service used by the Automaton Library.
	 * 
	 * @return dfa
	 * 		A NestedWordAutomaton that represents a DFA.
	 */
	public static NestedWordAutomaton<Character, String> emptyDfa(AutomataLibraryServices service){
		VpAlphabet<Character> alpha = alphabet();
		
		NestedWordAutomaton<Character, String> dfa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
	
		return dfa;
	}
	
	/**
	 * Creates a DFA with one state.
	 * 
	 * @param service the service used by the Automaton Library.
	 * 
	 * @return dfa
	 * 	A NestedWordAutomaton that represents a DFA.
	 */
	public static NestedWordAutomaton<Character, String> oneStateDfa(AutomataLibraryServices service){
		VpAlphabet<Character> alpha = alphabet('a');
		
		NestedWordAutomaton<Character, String> dfa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
	
		dfa.addState(true, true, "q0");
		addTransitionForAll(dfa, alpha, "q0", "q0");
		
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
	
	private static void addTransitionForAll(NestedWordAutomaton<Character, String> fsa, VpAlphabet<Character> alpha, String src, String... dests) {
		Set<Character> alphabet = alpha.getInternalAlphabet();
		assert alphabet.size() == dests.length;
		
		List<String> letters = Arrays.asList(dests);
		
		Iterator<Character> letterIterator = alphabet.iterator();
		Iterator<String> stateIterator = letters.iterator();
		
		while (letterIterator.hasNext() && stateIterator.hasNext()) {
			Character letter = letterIterator.next();
			String state = stateIterator.next();
			
			fsa.addInternalTransition(src, letter, state);
		}
	}
}

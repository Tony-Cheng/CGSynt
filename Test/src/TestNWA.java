import de.uni_freiburg.informatik.ultimate.automata.nestedword.*;
import de.uni_freiburg.informatik.ultimate.test.mocks.*;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.Word;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Accepts;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.automatondeltadebugger.factories.NestedWordAutomatonFactory;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateServiceProviderMock;;

public class TestNWA {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

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

		INestedWordAutomaton<Character, String> dfa = null;
		try {
			Determinize<Character, String> det = new Determinize<>(service,
					(IDeterminizeStateFactory) nwa.getStateFactory(), nwa);
			dfa = det.getResult();
			for (String state: dfa.getStates()) {
				System.out.println(state);
			}
		} catch (AutomataOperationCanceledException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		NestedWord<Character> word1 = NestedWord.nestedWord(new Word<>('a', 'b', 'b', 'c'));
		NestedWord<Character> word2 = NestedWord.nestedWord(new Word<>('b', 'a', 'a', 'a', 'b'));

		try {
			Accepts<Character, String> accept1 = new Accepts<>(service, dfa, word1);
			Accepts<Character, String> accept2 = new Accepts<>(service, dfa, word2);

			System.out.println(word1.toString() + " is accepted? " + accept1.getResult());
			System.out.println(word2.toString() + " is accepted? " + accept2.getResult());

		} catch (AutomataLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
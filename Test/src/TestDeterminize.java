import java.util.HashSet;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestDeterminize {

	public static void main(String[] args) throws AutomataOperationCanceledException {
		Set<Character> letters = new HashSet<>();
		letters.add('a');
		letters.add('b');
		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices autService = new AutomataLibraryServices(service);
		NestedWordAutomaton<Character, String> pi = new NestedWordAutomaton<>(autService, alpha, new StringFactory());
		pi.addState(true, false, "true");
		pi.addState(false, true, "false");

		Determinize<Character, String> determinize = new Determinize<>(autService,
				(IDeterminizeStateFactory<String>) pi.getStateFactory(), pi);

		INestedWordAutomaton<Character, String> determinizedAut = determinize.getResult();
		for (String state : determinizedAut.getStates()) {
			System.out.println(state);
		}
		
	}

}

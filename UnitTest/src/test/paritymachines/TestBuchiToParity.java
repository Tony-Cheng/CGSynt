package test.paritymachines;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.operations.BuchiToParity;
import cgsynt.tree.parity.ParityState;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import test.fsa.FsaFactory;

public class TestBuchiToParity {
	
	@Test
	public void test1() {
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices library = new AutomataLibraryServices(service);
		
		NestedWordAutomaton<Character, String> buchi = FsaFactory.fiveStateDFA(library);
		
		BuchiToParity<Character, String> convert = new BuchiToParity<>(buchi, library);
		ParityAutomaton<Character, ParityState<String>> parity = convert.getResult();
		
		for (ParityState<String> state : parity.getStates()) {
			if (parity.isFinal(state))
				assert state.getRank() == 0;
			else
				assert state.getRank() == 1;
		}
	}
	
	@Test 
	public void test2() {
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices library = new AutomataLibraryServices(service);
		
		NestedWordAutomaton<Character, String> buchi = FsaFactory.oneStateDfa(library);
		
		BuchiToParity<Character, String> convert = new BuchiToParity<>(buchi, library);
		ParityAutomaton<Character, ParityState<String>> parity = convert.getResult();
		
		for (ParityState<String> state : parity.getStates()) {
			if (parity.isFinal(state))
				assert state.getRank() == 0;
			else
				assert state.getRank() == 1;
		}
	}
}

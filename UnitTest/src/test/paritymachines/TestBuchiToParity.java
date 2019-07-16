package test.paritymachines;

import java.util.Map;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.operations.BuchiToParity;
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
		
		BuchiToParity<Character, String> convert = new BuchiToParity<>(buchi);
		ParityAutomaton<Character, String> parity = convert.getResult();
		
		Map<String, Integer> colouringFunction = parity.getColouringFunction();
		
		System.out.println(colouringFunction);
	}
}

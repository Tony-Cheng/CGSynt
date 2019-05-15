package test.fsa.operations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import test.fsa.FsaFactory;

class TestDfaToLta {
	private static IUltimateServiceProvider mock;
	private static AutomataLibraryServices service;
	
	@BeforeAll
	public void init() {
		mock = UltimateMocks.createUltimateServiceProviderMock();
		service = new AutomataLibraryServices(mock);
	}
	
	@Test
	void testDfaToLta() {
		NestedWordAutomaton<Character, String> fiveStateDFA = FsaFactory.fiveStateDFA(service);
		
		
	}

}

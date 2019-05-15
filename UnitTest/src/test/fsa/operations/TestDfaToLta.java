package test.fsa.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import dfa.operations.DfaToLta;
import test.fsa.FsaFactory;
import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.lta.LtaBool;

class TestDfaToLta {
	private static IUltimateServiceProvider mock;
	private static AutomataLibraryServices service;
	
	@BeforeAll
	public void init() {
		mock = UltimateMocks.createUltimateServiceProviderMock();
		service = new AutomataLibraryServices(mock);
	}
	
	/**
	 * Check that converting the five state DFA to an LTA works.
	 */
	@Test
	void testDfaToLta() {
		NestedWordAutomaton<Character, String> dfa = FsaFactory.fiveStateDFA(service);
		
		DfaToLta<Character, String> op = new DfaToLta<>(dfa);
		
		BuchiTreeAutomaton<LtaBool, String> lta = op.getResult();
		
		Set<String> initialStates = dfa.getInitialStates();
		Set<String> finalStates = dfa.getFinalStates();
		
		
	}

}

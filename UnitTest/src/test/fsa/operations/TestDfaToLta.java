package test.fsa.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import usra.dfa.operations.DfaToLtaPowerSet;
import test.fsa.FsaFactory;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.BuchiTreeAutomatonRule;
import usra.tree.buchi.lta.LtaBool;

class TestDfaToLta {
	private static IUltimateServiceProvider mock;
	private static AutomataLibraryServices service;
	
	@BeforeAll
	public static void init() {
		mock = UltimateMocks.createUltimateServiceProviderMock();
		service = new AutomataLibraryServices(mock);
	}
	
	/**
	 * Check that converting the five state DFA to an LTA works.
	 */
	@Test
	void testDfaToLtaOnFiveStateDfa() {
		NestedWordAutomaton<Character, String> dfa = FsaFactory.fiveStateDFA(service);
		
		DfaToLtaPowerSet<Character, String> op = new DfaToLtaPowerSet<>(dfa);
		
		BuchiTreeAutomaton<LtaBool, String> lta = op.getResult();
		
		Set<String> states = dfa.getStates();
		for (String state : states) {
			Collection<BuchiTreeAutomatonRule<LtaBool, String>> rules = lta.getRulesBySource(state);
				
			if (dfa.isFinal(state))
				assertEquals(rules.size(), 2);
			else
				assertEquals(rules.size(), 1);
		}
	}
	
	@Test
	void testDfaToLtaOnEmptyDfa() {
		NestedWordAutomaton<Character, String> dfa = FsaFactory.emptyDfa(service);
		
		DfaToLtaPowerSet<Character, String> op = new DfaToLtaPowerSet<>(dfa);
		
		BuchiTreeAutomaton<LtaBool, String> lta = op.getResult();
		
		Set<String> states = dfa.getStates();
		for (String state : states) {
			Collection<BuchiTreeAutomatonRule<LtaBool, String>> rules = lta.getRulesBySource(state);
				
			if (dfa.isFinal(state))
				assertEquals(rules.size(), 2);
			else
				assertEquals(rules.size(), 1);
		}
	}
	
	@Test
	void testDfaToLtaOnSingleStateDfa() {
		NestedWordAutomaton<Character, String> dfa = FsaFactory.oneStateDfa(service);
		
		DfaToLtaPowerSet<Character, String> op = new DfaToLtaPowerSet<>(dfa);
		
		BuchiTreeAutomaton<LtaBool, String> lta = op.getResult();
		
		Set<String> states = dfa.getStates();
		for (String state : states) {
			Collection<BuchiTreeAutomatonRule<LtaBool, String>> rules = lta.getRulesBySource(state);
				
			if (dfa.isFinal(state))
				assertEquals(rules.size(), 2);
			else
				assertEquals(rules.size(), 1);
		}
	}

}

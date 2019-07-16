package test.paritymachines;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.operations.BuchiToParity;
import cgsynt.dfa.parity.operations.ParityAutomatonToTree;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import test.fsa.FsaFactory;

public class TestParityAutomatonToTree {
	@Test
	public void test1() {
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices library = new AutomataLibraryServices(service);
		
		NestedWordAutomaton<Character, String> buchi = FsaFactory.sparseDfa(library);
		
		BuchiToParity<Character, String> convertToPA = new BuchiToParity<>(buchi, library);
		
		ParityState<String> deadState = new ParityState<>("deadState", 1);
		List<Character> ordering = new ArrayList<>();
		ordering.add('a');
		ordering.add('b');
		
		RankedBool.setRank(2);
		ParityAutomaton<Character, ParityState<String>> parityAutomaton = convertToPA.getResult();
		ParityAutomatonToTree<Character, ParityState<String>> convertToTree = 
				new ParityAutomatonToTree<>(parityAutomaton, ordering, deadState);
		
		ParityTreeAutomaton<RankedBool, ParityState<String>> tree = convertToTree.getResult();
		System.out.println(tree);
	}
	
	@Test
	public void test2() {
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices library = new AutomataLibraryServices(service);
		
		NestedWordAutomaton<Character, String> buchi = FsaFactory.fiveStateDFA(library);
		
		BuchiToParity<Character, String> convertToPA = new BuchiToParity<>(buchi, library);
		
		ParityState<String> deadState = new ParityState<>("deadState", 1);
		List<Character> ordering = new ArrayList<>();
		ordering.add('a');
		ordering.add('b');
		
		RankedBool.setRank(2);
		ParityAutomaton<Character, ParityState<String>> parityAutomaton = convertToPA.getResult();
		ParityAutomatonToTree<Character, ParityState<String>> convertToTree = 
				new ParityAutomatonToTree<>(parityAutomaton, ordering, deadState);
		
		ParityTreeAutomaton<RankedBool, ParityState<String>> tree = convertToTree.getResult();
		System.out.println(tree);
	}
}

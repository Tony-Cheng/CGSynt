package test.dfa.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.nfa.operations.NFACounterexample;
import cgsynt.nfa.operations.NFACounterexampleGeneration;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestCounterexampleGeneration {

	@Test
	void test1() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		ParityAutomaton<Character, IParityState> nwa = new ParityAutomaton<>(service, alpha, new ParityStateFactory());

		ParityState<String> q0 = new ParityState<String>("q0", 0);
		nwa.addState(true, true, q0);
		nwa.addInternalTransition(q0, 'a', q0);
		NFACounterexampleGeneration<Character> generation = new NFACounterexampleGeneration<>(nwa, 2);
		generation.computeResult();

		List<NFACounterexample<Character, IParityState>> counterexamples = generation.getResult();

		for (NFACounterexample<Character, IParityState> counterexample : counterexamples) {
			System.out.println("Test 1");
			System.out.println("Stem");
			while (!counterexample.stemTransitions.isEmpty()) {
				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
			}
			System.out.println(counterexample.stemStates.pop());
			System.out.println("Loop");
			while (!counterexample.loopTransitions.isEmpty()) {
				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
			}
			System.out.println(counterexample.loopStates.pop());
		}
	}
	
//	@Test
//	void test2() {
//		// Stem: q0 loop: q0 q1 q2
//		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
//		AutomataLibraryServices service = new AutomataLibraryServices(mock);
//
//		Set<Character> letters = new HashSet<Character>();
//		letters.add('a');
//		letters.add('b');
//		letters.add('c');
//
//		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
//
//		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
//
//		nwa.addState(true, true, "q0");
//		nwa.addState(false, false, "q1");
//		nwa.addState(false, false, "q2");
//
//		nwa.addInternalTransition("q0", 'a', "q1");
//		nwa.addInternalTransition("q1", 'b', "q2");
//		nwa.addInternalTransition("q2", 'a', "q0");
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 3);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		for (NFACounterexample<Character, String> counterexample : counterexamples) {
//			System.out.println("Test 2");
//			System.out.println("Stem");
//			while (!counterexample.stemTransitions.isEmpty()) {
//				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
//			}
//			System.out.println(counterexample.stemStates.pop());
//			System.out.println("Loop");
//			while (!counterexample.loopTransitions.isEmpty()) {
//				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
//			}
//			System.out.println(counterexample.loopStates.pop());
//		}
//	}
//	
//	@Test
//	void test3() {
//		// no final state no counterexample
//		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
//		AutomataLibraryServices service = new AutomataLibraryServices(mock);
//
//		Set<Character> letters = new HashSet<Character>();
//		letters.add('a');
//		letters.add('b');
//		letters.add('c');
//
//		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
//
//		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
//
//		nwa.addState(true, false, "q0");
//		nwa.addState(false, false, "q1");
//		nwa.addState(false, false, "q2");
//
//		nwa.addInternalTransition("q0", 'a', "q1");
//		nwa.addInternalTransition("q1", 'b', "q2");
//		nwa.addInternalTransition("q2", 'a', "q0");
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 3);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		for (NFACounterexample<Character, String> counterexample : counterexamples) {
//			System.out.println("Test 3");
//			System.out.println("Stem");
//			while (!counterexample.stemTransitions.isEmpty()) {
//				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
//			}
//			System.out.println(counterexample.stemStates.pop());
//			System.out.println("Loop");
//			while (!counterexample.loopTransitions.isEmpty()) {
//				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
//			}
//			System.out.println(counterexample.loopStates.pop());
//		}
//	}
//	
//	@Test
//	void test4() {
//		// stem q0 q1 loop q1 q2
//		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
//		AutomataLibraryServices service = new AutomataLibraryServices(mock);
//
//		Set<Character> letters = new HashSet<Character>();
//		letters.add('a');
//		letters.add('b');
//		letters.add('c');
//
//		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
//
//		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
//
//		nwa.addState(true, false, "q0");
//		nwa.addState(false, false, "q1");
//		nwa.addState(false, true, "q2");
//
//		nwa.addInternalTransition("q0", 'a', "q1");
//		nwa.addInternalTransition("q1", 'b', "q2");
//		nwa.addInternalTransition("q2", 'a', "q1");
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 3);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		for (NFACounterexample<Character, String> counterexample : counterexamples) {
//			System.out.println("Test 4");
//			System.out.println("Stem");
//			while (!counterexample.stemTransitions.isEmpty()) {
//				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
//			}
//			System.out.println(counterexample.stemStates.pop());
//			System.out.println("Loop");
//			while (!counterexample.loopTransitions.isEmpty()) {
//				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
//			}
//			System.out.println(counterexample.loopStates.pop());
//		}
//	}
//	
//	@Test
//	void test5() {
//		// stem q0 q1 loop q1; stem q0 q2 loop q2
//		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
//		AutomataLibraryServices service = new AutomataLibraryServices(mock);
//
//		Set<Character> letters = new HashSet<Character>();
//		letters.add('a');
//		letters.add('b');
//		letters.add('c');
//
//		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
//
//		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
//
//		nwa.addState(true, false, "q0");
//		nwa.addState(false, true, "q1");
//		nwa.addState(false, true, "q2");
//
//		nwa.addInternalTransition("q0", 'a', "q1");
//		nwa.addInternalTransition("q0", 'b', "q2");
//		nwa.addInternalTransition("q1", 'b', "q1");
//		nwa.addInternalTransition("q2", 'c', "q2");
//
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		for (NFACounterexample<Character, String> counterexample : counterexamples) {
//			System.out.println("Test 5");
//			System.out.println("Stem");
//			while (!counterexample.stemTransitions.isEmpty()) {
//				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
//			}
//			System.out.println(counterexample.stemStates.pop());
//			System.out.println("Loop");
//			while (!counterexample.loopTransitions.isEmpty()) {
//				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
//			}
//			System.out.println(counterexample.loopStates.pop());
//		}
//	}
//	
//	@Test
//	void test6() {
//		// no counterexample
//		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
//		AutomataLibraryServices service = new AutomataLibraryServices(mock);
//
//		Set<Character> letters = new HashSet<Character>();
//		letters.add('a');
//		letters.add('b');
//		letters.add('c');
//
//		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
//
//		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
//
//		nwa.addState(true, true, "q0");
//		nwa.addState(false, false, "q1");
//		nwa.addState(false, false, "q2");
//
//		nwa.addInternalTransition("q0", 'a', "q1");
//		nwa.addInternalTransition("q0", 'b', "q2");
//		nwa.addInternalTransition("q1", 'b', "q1");
//		nwa.addInternalTransition("q2", 'c', "q2");
//
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		for (NFACounterexample<Character, String> counterexample : counterexamples) {
//			System.out.println("Test 6");
//			System.out.println("Stem");
//			while (!counterexample.stemTransitions.isEmpty()) {
//				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
//			}
//			System.out.println(counterexample.stemStates.pop());
//			System.out.println("Loop");
//			while (!counterexample.loopTransitions.isEmpty()) {
//				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
//			}
//			System.out.println(counterexample.loopStates.pop());
//		}
//	}
//	
//	@Test
//	void test7() {
//		// stem q0 q1 loop q1
//		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
//		AutomataLibraryServices service = new AutomataLibraryServices(mock);
//
//		Set<Character> letters = new HashSet<Character>();
//		letters.add('a');
//		letters.add('b');
//		letters.add('c');
//
//		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);
//
//		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());
//
//		nwa.addState(true, false, "q0");
//		nwa.addState(false, true, "q1");
//		nwa.addState(false, false, "q2");
//
//		nwa.addInternalTransition("q0", 'a', "q1");
//		nwa.addInternalTransition("q0", 'b', "q2");
//		nwa.addInternalTransition("q1", 'b', "q1");
//		nwa.addInternalTransition("q2", 'c', "q2");
//
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		for (NFACounterexample<Character, String> counterexample : counterexamples) {
//			System.out.println("Test 7");
//			System.out.println("Stem");
//			while (!counterexample.stemTransitions.isEmpty()) {
//				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
//			}
//			System.out.println(counterexample.stemStates.pop());
//			System.out.println("Loop");
//			while (!counterexample.loopTransitions.isEmpty()) {
//				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
//			}
//			System.out.println(counterexample.loopStates.pop());
//		}
//	}

}

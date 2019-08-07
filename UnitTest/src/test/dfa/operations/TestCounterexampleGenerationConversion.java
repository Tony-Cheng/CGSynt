package test.dfa.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.parity.operations.ParityCounterexample;
import cgsynt.dfa.parity.operations.ParityCounterexampleGeneration;
import cgsynt.operations.CounterexampleToLassoRun2;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

/**
 * This operation tested is not used anymore.
 *
 */
public class TestCounterexampleGenerationConversion {

//	@Test
//	void test1() {
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
//		nwa.addInternalTransition("q0", 'a', "q0");
//
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 1");
//			System.out.println(lassoRun);
//		}
//	}
//
//	@Test
//	void test2() {
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
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 2");
//			System.out.println(lassoRun);
//		}
//	}
//
//	@Test
//	void test3() {
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
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 3");
//			System.out.println(lassoRun);
//		}
//	}
//
//	@Test
//	void test4() {
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
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 4");
//			System.out.println(lassoRun);
//		}
//	}
//
//	@Test
//	void test5() {
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
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 5");
//			System.out.println(lassoRun);
//		}
//	}
//
//	@Test
//	void test6() {
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
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 6");
//			System.out.println(lassoRun);
//		}
//	}
//
//	@Test
//	void test7() {
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
//		NFACounterexampleGeneration<Character, String> generation = new NFACounterexampleGeneration<>(nwa, 2);
//		generation.computeResult();
//
//		List<NFACounterexample<Character, String>> counterexamples = generation.getResult();
//
//		CounterexampleToLassoRun2<Character, String> computeLassoRun = new CounterexampleToLassoRun2<Character, String>(
//				counterexamples);
//
//		computeLassoRun.computeResult();
//
//		List<NestedLassoRun<Character, String>> lassoRuns = computeLassoRun.getResult();
//		for (NestedLassoRun<Character, String> lassoRun : lassoRuns) {
//			System.out.println("Test 7");
//			System.out.println(lassoRun);
//		}
//	}
}

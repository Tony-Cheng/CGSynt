package test.tree.buchi.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class CounterexamplesGeneratorTest {

	@Test
	void test1() {
		List<Character> letters = new ArrayList<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');
		VpAlphabet<Character> alpha = new VpAlphabet<>(new HashSet<>(letters));

		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<Character, String>(service, alpha,
				new StringFactory());
		nwa.addState(true, true, "A");
		nwa.addInternalTransition("A", 'a', "A");
		CounterexamplesGeneration<Character, String> generation = new CounterexamplesGeneration<>(nwa, 7, letters);
		generation.computeResult();
		System.out.println("Test 1");
		System.out.println("Number of counterexamples: " + generation.getResult().size());
		for (List<Character> next : generation.getResult()) {
			System.out.println(next);
		}
	}

	@Test
	void test2() {
		List<Character> letters = new ArrayList<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');
		VpAlphabet<Character> alpha = new VpAlphabet<>(new HashSet<>(letters));

		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<Character, String>(service, alpha,
				new StringFactory());
		nwa.addState(true, false, "A");
		nwa.addInternalTransition("A", 'a', "A");

		CounterexamplesGeneration<Character, String> generation = new CounterexamplesGeneration<>(nwa, 7, letters);
		generation.computeResult();
		System.out.println("Test 2");
		System.out.println("Number of counterexamples: " + generation.getResult().size());
		for (List<Character> next : generation.getResult()) {
			System.out.println(next);
		}
	}

	@Test
	void test3() {
		List<Character> letters = new ArrayList<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');
		VpAlphabet<Character> alpha = new VpAlphabet<>(new HashSet<>(letters));

		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<Character, String>(service, alpha,
				new StringFactory());
		nwa.addState(false, false, "A");
		nwa.addInternalTransition("A", 'a', "A");
		CounterexamplesGeneration<Character, String> generation = new CounterexamplesGeneration<>(nwa, 7, letters);
		generation.computeResult();
		System.out.println("Test 3");
		System.out.println("Number of counterexamples: " + generation.getResult().size());
		for (List<Character> next : generation.getResult()) {
			System.out.println(next);
		}
	}

	@Test
	void test4() {
		List<Character> letters = new ArrayList<Character>();
		letters.add('a');
		letters.add('b');
		letters.add('c');
		VpAlphabet<Character> alpha = new VpAlphabet<>(new HashSet<>(letters));

		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<Character, String>(service, alpha,
				new StringFactory());
		nwa.addState(true, false, "A");
		nwa.addInternalTransition("A", 'a', "A");
		nwa.addInternalTransition("A", 'b', "A");
		CounterexamplesGeneration<Character, String> generation = new CounterexamplesGeneration<>(nwa, 3, letters);
		generation.computeResult();
		System.out.println("Test 4");
		System.out.println("Number of counterexamples: " + generation.getResult().size());
		for (List<Character> next : generation.getResult()) {
			System.out.println(next);
		}
	}
}

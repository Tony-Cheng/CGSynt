package test.buchi.determinization;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.buchi.determinization.BuchiDeterminization;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestBuchiDeterminization {

	@Test
	void test1() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		// letters.add('b');
		// letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, true, "q0");

		nwa.addInternalTransition("q0", 'a', "q0");

		BuchiDeterminization<Character, String> determinization = new BuchiDeterminization<>(nwa, service,
				new ParityStateFactory());
		determinization.computeResult();
		System.out.println("Test 1");
		System.out.println(determinization.getResult());
	}
	
	@Test
	void test2() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		// letters.add('b');
		// letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");

		nwa.addInternalTransition("q0", 'a', "q0");

		BuchiDeterminization<Character, String> determinization = new BuchiDeterminization<>(nwa, service,
				new ParityStateFactory());
		determinization.computeResult();
		System.out.println("Test 2");
		System.out.println(determinization.getResult());
	}
	
	@Test
	void test3() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		// letters.add('b');
		// letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, true, "q1");

		nwa.addInternalTransition("q0", 'a', "q0");
		nwa.addInternalTransition("q0", 'a', "q1");


		BuchiDeterminization<Character, String> determinization = new BuchiDeterminization<>(nwa, service,
				new ParityStateFactory());
		determinization.computeResult();
		System.out.println("Test 3");
		System.out.println(determinization.getResult());
	}
	
	@Test
	void test4() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		letters.add('b');
		// letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, true, "q1");

		nwa.addInternalTransition("q0", 'a', "q0");
		nwa.addInternalTransition("q0", 'a', "q1");


		BuchiDeterminization<Character, String> determinization = new BuchiDeterminization<>(nwa, service,
				new ParityStateFactory());
		determinization.computeResult();
		System.out.println("Test 4");
		System.out.println(determinization.getResult());
	}
	
	@Test
	void test5() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);

		Set<Character> letters = new HashSet<Character>();
		letters.add('a');
		letters.add('b');
		// letters.add('c');

		VpAlphabet<Character> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Character, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, true, "q1");

		nwa.addInternalTransition("q0", 'a', "q0");
		nwa.addInternalTransition("q0", 'a', "q1");
		nwa.addInternalTransition("q1", 'b', "q1");



		BuchiDeterminization<Character, String> determinization = new BuchiDeterminization<>(nwa, service,
				new ParityStateFactory());
		determinization.computeResult();
		System.out.println("Test 5");
		System.out.println(determinization.getResult());
	}
}

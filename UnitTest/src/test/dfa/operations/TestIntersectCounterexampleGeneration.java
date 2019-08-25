package test.dfa.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.intersect.DfaParityIntersectAutomaton;
import cgsynt.dfa.parity.intersect.operations.DfaParityCounterexample;
import cgsynt.dfa.parity.intersect.operations.DfaParityCounterexampleGeneration;
import cgsynt.dfa.parity.operations.ParityComplementAndCounterexampleGeneration;
import cgsynt.dfa.parity.operations.ParityCounterexample;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestIntersectCounterexampleGeneration {

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

		ParityState<String> q0 = new ParityState<String>("q0", 1);
		nwa.addState(true, true, q0);
		nwa.addInternalTransition(q0, 'a', q0);

		NestedWordAutomaton<Character, String> acceptedAutomaton = new NestedWordAutomaton<>(service, alpha,
				new StringFactory());
		String acceptedState = "accepted state";
		acceptedAutomaton.addState(true, true, acceptedState);
		acceptedAutomaton.addInternalTransition(acceptedState, 'a', acceptedState);
		acceptedAutomaton.addInternalTransition(acceptedState, 'b', acceptedState);
		acceptedAutomaton.addInternalTransition(acceptedState, 'c', acceptedState);

		DfaParityIntersectAutomaton<Character, String, IParityState> intersectedAut = new DfaParityIntersectAutomaton<>(
				acceptedAutomaton, nwa);
		DfaParityCounterexampleGeneration<Character, String, IParityState> generation = new DfaParityCounterexampleGeneration<>(
				intersectedAut, 2);
		generation.computeResult();

		List<DfaParityCounterexample<Character, String, IParityState>> counterexamples = generation.getResult();

		for (DfaParityCounterexample<Character, String, IParityState> counterexample : counterexamples) {
			System.out.println("Test 1");
			System.out.println("Stem states: " + counterexample.stemStates.size() + " Stem transitions: "
					+ counterexample.stemTransitions.size() + " Loop states: " + counterexample.loopStates.size()
					+ " Loop transitions: " + counterexample.loopTransitions.size());
			System.out.println("Stem");
			while (!counterexample.stemTransitions.isEmpty()) {
				System.out.println(counterexample.stemStates.pop() + " " + counterexample.stemTransitions.pop());
			}
			System.out.println(counterexample.stemStates.pop());
			System.out.println("Loop");
			while (!counterexample.loopTransitions.isEmpty()) {
				System.out.println(counterexample.loopStates.pop() + " " + counterexample.loopTransitions.pop());
			}
		}
	}
}

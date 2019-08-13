package test.buchi.parity.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.parity.BuchiParityHybridIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityHybridIntersectState;
import cgsynt.tree.buchi.parity.operations.BuchiParityCounterexampleGeneration;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;

public class TestCounterexampleGeneration {

	@Test
	void test1() {
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";
		ParityState<String> ps1 = new ParityState<>("parity state 1", 0);

		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		aut2.addInitState(ps1);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs1);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps1);
		plist1.add(ps1);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);

		aut1.addRule(brule1);
		aut2.addRule(prule1);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		List<String> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add("Transition 1");
		transitionAlphabet.add("Transition 2");

		BuchiParityCounterexampleGeneration<RankedBool, String, ParityState<String>, String> counterexampleGeneration = new BuchiParityCounterexampleGeneration<>(
				aut3, 4, transitionAlphabet);
		counterexampleGeneration.computeResult();
		List<Stack<String>> transitions = counterexampleGeneration.getResultTransition();
		List<Stack<BuchiParityHybridIntersectState<String, ParityState<String>>>> states = counterexampleGeneration
				.getResultStates();
		System.out.println("Test 1");
		for (int i = 0; i < transitions.size(); i++) {
			System.out.println("Iteration " + i);
			Stack<String> transition = transitions.get(i);
			Stack<BuchiParityHybridIntersectState<String, ParityState<String>>> state = states.get(i);
			while(transition.size() > 0) {
				System.out.println("State: " + state.pop() + " Transition: " + transition.pop());
			}
			while(state.size() > 0) {
				System.out.println(state.pop());
			}
			System.out.println();
		}
	}
	
	@Test
	void test2() {
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";
		String bs2 = "buchi state 2";
		ParityState<String> ps1 = new ParityState<>("parity state 1", 0);

		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		aut2.addInitState(ps1);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs2);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps1);
		plist1.add(ps1);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs2, blist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut2.addRule(prule1);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		List<String> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add("Transition 1");
		transitionAlphabet.add("Transition 2");

		BuchiParityCounterexampleGeneration<RankedBool, String, ParityState<String>, String> counterexampleGeneration = new BuchiParityCounterexampleGeneration<>(
				aut3, 4, transitionAlphabet);
		counterexampleGeneration.computeResult();
		List<Stack<String>> transitions = counterexampleGeneration.getResultTransition();
		List<Stack<BuchiParityHybridIntersectState<String, ParityState<String>>>> states = counterexampleGeneration
				.getResultStates();
		System.out.println("Test 2");
		for (int i = 0; i < transitions.size(); i++) {
			System.out.println("Iteration " + i);
			Stack<String> transition = transitions.get(i);
			Stack<BuchiParityHybridIntersectState<String, ParityState<String>>> state = states.get(i);
			while(transition.size() > 0) {
				System.out.println("State: " + state.pop() + " Transition: " + transition.pop());
			}
			while (state.size() > 0) {
				System.out.println(state.pop());
			}
			System.out.println();
		}
	}
	
	@Test
	void test3() {
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";
		String bs2 = "buchi state 2";
		ParityState<String> ps1 = new ParityState<>("parity state 1", 0);
		ParityState<String> ps2 = new ParityState<>("parity state 2", 1);


		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		aut2.addInitState(ps1);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs2);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps1);
		plist1.add(ps2);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs2, blist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps2, plist1);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut2.addRule(prule1);
		aut2.addRule(prule2);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		List<String> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add("Transition 1");
		transitionAlphabet.add("Transition 2");

		BuchiParityCounterexampleGeneration<RankedBool, String, ParityState<String>, String> counterexampleGeneration = new BuchiParityCounterexampleGeneration<>(
				aut3, 4, transitionAlphabet);
		counterexampleGeneration.computeResult();
		List<Stack<String>> transitions = counterexampleGeneration.getResultTransition();
		List<Stack<BuchiParityHybridIntersectState<String, ParityState<String>>>> states = counterexampleGeneration
				.getResultStates();
		System.out.println("Test 3");
		for (int i = 0; i < transitions.size(); i++) {
			System.out.println("Iteration " + i);
			Stack<String> transition = transitions.get(i);
			Stack<BuchiParityHybridIntersectState<String, ParityState<String>>> state = states.get(i);
			while(transition.size() > 0) {
				System.out.println("State: " + state.pop() + " Transition: " + transition.pop());
			}
			while (state.size() > 0) {
				System.out.println(state.pop());
			}
			System.out.println();
		}
	}
}

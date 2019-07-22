package test.buchi.parity.operations;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;

public class TestBuchiParityIntersection {
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

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut1, aut2);

		System.out.println("Test 1");
		System.out.print(aut3);

	}

	@Test
	void test2() {
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
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps1, plist1);

		aut1.addRule(brule1);
		aut2.addRule(prule1);

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut1, aut2);

		System.out.println("Test 2");
		System.out.print(aut3);

	}

	@Test
	void test3() {
		// Empty buchi aut
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

		List<String> blist2 = new ArrayList<>();
		blist2.add(bs1);
		blist2.add(bs2);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps1);
		plist1.add(ps1);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs2, blist2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut2.addRule(prule1);

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut1, aut2);

		System.out.println("Test 3");
		System.out.print(aut3);

	}

	@Test
	void test4() {
		// Empty parity aut
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";

		ParityState<String> ps0 = new ParityState<>("parity state 0", 0);
		ParityState<String> ps1 = new ParityState<>("parity state 1", 1);

		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		aut2.addInitState(ps1);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs1);

		List<ParityState<String>> plist0 = new ArrayList<>();
		plist0.add(ps0);
		plist0.add(ps1);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps0);
		plist1.add(ps0);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule0 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps0, plist0);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);

		aut1.addRule(brule1);
		aut2.addRule(prule0);
		aut2.addRule(prule1);

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut1, aut2);

		System.out.println("Test 4");
		System.out.print(aut3);

	}
	
	@Test
	void test5() {
		// Non-empty parity aut
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";

		ParityState<String> ps0 = new ParityState<>("parity state 0", 2);
		ParityState<String> ps1 = new ParityState<>("parity state 1", 1);

		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		aut2.addInitState(ps1);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs1);

		List<ParityState<String>> plist0 = new ArrayList<>();
		plist0.add(ps0);
		plist0.add(ps1);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps0);
		plist1.add(ps0);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule0 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps0, plist0);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);

		aut1.addRule(brule1);
		aut2.addRule(prule0);
		aut2.addRule(prule1);

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut1, aut2);

		System.out.println("Test 4");
		System.out.print(aut3);

	}
}

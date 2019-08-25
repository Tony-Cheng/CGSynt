package test.buchi.parity.operations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.parity.BuchiParityHybridIntersectAutomaton;
import cgsynt.tree.buchi.parity.operations.BuchiParityEmptinessCheck;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import test.tree.buchi.RankedLetter;

public class TestBuchiParityEmptinessCheck {

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

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
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

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());

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

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
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

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());

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

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}

	@Test
	void test6() {
		// A non-empty buchi and a non-empty parity automatons, but their intersection
		// is empty
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs0 = "buchi state 0";
		String bs1 = "buchi state 1";

		ParityState<String> ps0 = new ParityState<>("parity state 0", 2);
		ParityState<String> ps1 = new ParityState<>("parity state 1", 1);

		aut1.addInitState(bs0);
		aut1.addFinalState(bs0);
		aut2.addInitState(ps0);

		List<String> blist0 = new ArrayList<>();
		blist0.add(bs0);
		blist0.add(bs0);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs0);
		blist1.add(bs1);

		List<ParityState<String>> plist0 = new ArrayList<>();
		plist0.add(ps0);
		plist0.add(ps1);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps0);
		plist1.add(ps0);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs0, blist0);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs0, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule3 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs1, blist1);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule0 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps0, plist0);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps1, plist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist0);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut1.addRule(brule3);
		aut2.addRule(prule0);
		aut2.addRule(prule1);
		aut2.addRule(prule2);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}

	@Test
	void test7() {
		// A non-empty buchi and a non-empty parity automatons, and their intersection
		// is non-empty
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs0 = "buchi state 0";
		String bs1 = "buchi state 1";

		ParityState<String> ps0 = new ParityState<>("parity state 0", 2);
		ParityState<String> ps1 = new ParityState<>("parity state 1", 1);

		aut1.addInitState(bs0);
		aut1.addFinalState(bs0);
		aut2.addInitState(ps0);

		List<String> blist0 = new ArrayList<>();
		blist0.add(bs0);
		blist0.add(bs0);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs0);
		blist1.add(bs1);

		List<ParityState<String>> plist0 = new ArrayList<>();
		plist0.add(ps0);
		plist0.add(ps1);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps0);
		plist1.add(ps0);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs0, blist0);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs0, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule3 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs1, blist1);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule0 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps0, plist0);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps1, plist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps0, plist0);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut1.addRule(brule3);
		aut2.addRule(prule0);
		aut2.addRule(prule3);
		aut2.addRule(prule1);
		aut2.addRule(prule2);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());

	}

	@Test
	void test8() {
		// A non-empty buchi and a non-empty parity automatons, but their intersection
		// is non-empty
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";
		String bs2 = "buchi state 2";
		String bs3 = "buchi state 3";
		String bs4 = "buchi state 4";

		ParityState<String> ps1 = new ParityState<>("parity state 1", 1);
		ParityState<String> ps2 = new ParityState<>("parity state 2", 2);
		ParityState<String> ps3 = new ParityState<>("parity state 3", 3);
		ParityState<String> ps4 = new ParityState<>("parity state 4", 4);

		aut1.addInitState(bs1);
		aut2.addInitState(ps4);

		aut1.addFinalState(bs3);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs2);
		blist1.add(bs3);

		List<String> blist2 = new ArrayList<>();
		blist2.add(bs3);
		blist2.add(bs3);

		List<String> blist3 = new ArrayList<>();
		blist3.add(bs3);
		blist3.add(bs3);

		List<String> blist4 = new ArrayList<>();
		blist4.add(bs4);
		blist4.add(bs4);

		List<String> blist2p = new ArrayList<>();
		blist2p.add(bs4);
		blist2p.add(bs4);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps4);
		plist1.add(ps4);

		List<ParityState<String>> plist2 = new ArrayList<>();
		plist2.add(ps3);
		plist2.add(ps3);

		List<ParityState<String>> plist3 = new ArrayList<>();
		plist3.add(ps2);
		plist3.add(ps2);

		List<ParityState<String>> plist4 = new ArrayList<>();
		plist4.add(ps4);
		plist4.add(ps4);

		List<ParityState<String>> plist2p = new ArrayList<>();
		plist2p.add(ps4);
		plist2p.add(ps4);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs2, blist2);
		BuchiTreeAutomatonRule<RankedBool, String> brule3 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs3, blist3);
		BuchiTreeAutomatonRule<RankedBool, String> brule4 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs4, blist4);
		BuchiTreeAutomatonRule<RankedBool, String> brule2p = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs2,
				blist2p);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps1, plist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps2, plist2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps3, plist3);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule4 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				ps4, plist4);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2p = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps2, plist2p);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut1.addRule(brule3);
		aut1.addRule(brule4);
		aut1.addRule(brule2p);

		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addRule(prule4);
		aut2.addRule(prule2p);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());

	}
	
	@Test
	void test9() {
		// A non-empty buchi and a non-empty parity automatons, but their intersection
		// is empty
		RankedBool.setRank(2);
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);

		String bs1 = "buchi state 1";
		String bs2 = "buchi state 2";
		String bs3 = "buchi state 3";
		String bs4 = "buchi state 4";

		ParityState<String> ps1 = new ParityState<>("parity state 1", 1);
		ParityState<String> ps2 = new ParityState<>("parity state 2", 2);
		ParityState<String> ps3 = new ParityState<>("parity state 3", 3);
		ParityState<String> ps4 = new ParityState<>("parity state 4", 4);
		

		aut1.addInitState(bs1);
		aut2.addInitState(ps4);
		
		aut1.addFinalState(bs3);

		List<String> blist1 = new ArrayList<>();
		blist1.add(bs2);
		blist1.add(bs3);

		List<String> blist2 = new ArrayList<>();
		blist2.add(bs3);
		blist2.add(bs3);

		List<String> blist3 = new ArrayList<>();
		blist3.add(bs3);
		blist3.add(bs3);

		List<String> blist4 = new ArrayList<>();
		blist4.add(bs4);
		blist4.add(bs4);

		List<String> blist2p = new ArrayList<>();
		blist2p.add(bs4);
		blist2p.add(bs4);

		List<ParityState<String>> plist1 = new ArrayList<>();
		plist1.add(ps4);
		plist1.add(ps4);

		List<ParityState<String>> plist2 = new ArrayList<>();
		plist2.add(ps3);
		plist2.add(ps3);

		List<ParityState<String>> plist3 = new ArrayList<>();
		plist3.add(ps2);
		plist3.add(ps2);

		List<ParityState<String>> plist4 = new ArrayList<>();
		plist4.add(ps4);
		plist4.add(ps4);

		List<ParityState<String>> plist2p = new ArrayList<>();
		plist2p.add(ps4);
		plist2p.add(ps4);

		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		BuchiTreeAutomatonRule<RankedBool, String> brule2 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs2, blist2);
		BuchiTreeAutomatonRule<RankedBool, String> brule3 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs3, blist3);
		BuchiTreeAutomatonRule<RankedBool, String> brule4 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs4, blist4);
		BuchiTreeAutomatonRule<RankedBool, String> brule2p = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs2,
				blist2p);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				RankedBool.TRUE, ps1, plist1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, ps2, plist2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				RankedBool.TRUE, ps3, plist3);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule4 = new ParityTreeAutomatonRule<>(
				RankedBool.TRUE, ps4, plist4);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2p = new ParityTreeAutomatonRule<>(
				RankedBool.TRUE, ps2, plist2p);

		aut1.addRule(brule1);
		aut1.addRule(brule2);
		aut1.addRule(brule3);
		aut1.addRule(brule4);
		aut1.addRule(brule2p);

		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addRule(prule4);
		aut2.addRule(prule2p);

		BuchiParityHybridIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityHybridIntersectAutomaton<>(
				aut1, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());

	}
}
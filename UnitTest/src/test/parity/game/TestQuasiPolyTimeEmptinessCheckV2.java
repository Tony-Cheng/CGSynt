package test.parity.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.parity.games.ParityGame;
import cgsynt.parity.games.ParityGameEmptinessCheck;
import cgsynt.parity.games.ParityGameProgramExtraction;
import cgsynt.parity.games.QuasiTimeEmptinessCheck;
import cgsynt.parity.games.QuasiTimeEmptinessCheckV2;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;

public class TestQuasiPolyTimeEmptinessCheckV2 {

	@Test
	void test1() {
		// Test a simple nonempty automaton
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q0, list);

		aut.addRule(rule1);
		aut.addInitState(q0);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	@Test
	void test2() {
		// Test a simple empty automaton
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 1);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q0, list);

		aut.addRule(rule1);
		aut.addInitState(q0);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);

		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
	
	@Test
	void test3() {

		// nonempty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q2);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q1);
		list2.add(q1);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q2, list2);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addInitState(q1);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	@Test
	void test4() {
		// empty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 3);
		ParityState<String> q2 = new ParityState<String>("q2", 2);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q2);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q1);
		list2.add(q1);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q2, list2);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addInitState(q1);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
	
	@Test
	void test5() {
		// non-empty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q3 = new ParityState<String>("q3", 3);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q2);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q1);
		list2.add(q1);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q1);
		list3.add(q2);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q3, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addInitState(q3);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	@Test
	void test6() {
		// empty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q3 = new ParityState<String>("q3", 3);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q2);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q1);
		list2.add(q3);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q1);
		list3.add(q2);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q3, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addInitState(q3);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
	
	@Test
	void test7() {
		// nonempty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q0);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q2);
		list2.add(q2);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q0);
		list3.add(q0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule0 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q0, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule0);
		aut.addInitState(q1);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	@Test
	void test8() {
		// empty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q0);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q2);
		list2.add(q2);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q0);
		list3.add(q1);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule0 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q0, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule0);
		aut.addInitState(q1);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
	
	@Test
	void test9() {
		// nonempty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q0);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q1);
		list2.add(q2);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q0);
		list3.add(q0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule0 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q0, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule0);
		aut.addInitState(q1);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}

	@Test
	void test10() {
		// nonempty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q1 = new ParityState<String>("q1", 1);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		ParityState<String> q3 = new ParityState<String>("q3", 3);

		List<ParityState<String>> list0 = new ArrayList<>();
		list0.add(q1);
		list0.add(q2);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q2);
		list1.add(q2);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q2);
		list2.add(q2);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q0);
		list3.add(q0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule0 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q0, list0);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q1, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q3, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule0);
		aut.addRule(rule3);
		aut.addInitState(q3);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());

	}
	
	@Test
	void test11() {
		// nonempty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q4 = new ParityState<String>("q4", 4);
		ParityState<String> q3 = new ParityState<String>("q3", 3);

		List<ParityState<String>> list4 = new ArrayList<>();
		list4.add(q4);
		list4.add(q4);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q4);
		list2.add(q4);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q4);
		list3.add(q4);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule4 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q4, list4);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q3, list3);

		aut.addRule(rule2);
		aut.addRule(rule4);
		aut.addRule(rule3);
		aut.addInitState(q2);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	@Test
	void test12() {
		// nonempty
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q4 = new ParityState<String>("q4", 4);
		ParityState<String> q3 = new ParityState<String>("q3", 3);
		ParityState<String> q1 = new ParityState<String>("q1", 1);

		List<ParityState<String>> list4 = new ArrayList<>();
		list4.add(q4);
		list4.add(q4);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q3);
		list2.add(q1);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q4);
		list3.add(q4);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(q2);
		list1.add(q2);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule4 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q4, list4);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				q3, list3);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q1, list1);

		aut.addRule(rule2);
		aut.addRule(rule4);
		aut.addRule(rule3);
		aut.addRule(rule1);
		aut.addInitState(q2);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	// @Test
	// void test13() {
	// // An empty parity aut
	// RankedBool.setRank(2);
	// ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new
	// ParityTreeAutomaton<>(2);
	//
	// ParityState<String> ps0 = new ParityState<>("parity state 0", 0);
	// ParityState<String> ps1 = new ParityState<>("parity state 1", 1);
	//
	// aut.addInitState(ps1);
	//
	// List<ParityState<String>> plist0 = new ArrayList<>();
	// plist0.add(ps0);
	// plist0.add(ps1);
	//
	// List<ParityState<String>> plist1 = new ArrayList<>();
	// plist1.add(ps0);
	// plist1.add(ps0);
	//
	// ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule0 = new
	// ParityTreeAutomatonRule<>(RankedBool.TRUE,
	// ps0, plist0);
	// ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new
	// ParityTreeAutomatonRule<>(RankedBool.TRUE,
	// ps1, plist1);
	//
	// aut.addRule(prule0);
	// aut.addRule(prule1);
	//
	// ParityGame<RankedBool, ParityState<String>> parityGame = new
	// ParityGame<>(aut);
	// QuasiTimeEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new
	// QuasiTimeEmptinessCheck<>(
	// parityGame);
	// emptinessCheck.computeResult();
	// assertTrue(emptinessCheck.getResult());
	// }
	
	public void test14() {
		RankedBool.setRank(2);

		ParityState<String> q = new ParityState<>("q", 0);
		List<ParityState<String>> qt = new ArrayList<>();
		qt.add(q);
		qt.add(q);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q, qt);

		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		aut.addInitState(q);
		aut.addRule(rule);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertFalse(emptinessCheck.getResult());
	}
	
	@Test
	void test15() {
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> q2 = new ParityState<String>("q2", 2);
		ParityState<String> q3 = new ParityState<String>("q3", 3);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(q3);
		list2.add(q3);
		
		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(q2);
		list3.add(q2);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q2, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				q3, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addInitState(q2);

		ParityGame<RankedBool, ParityState<String>> parityGame = new ParityGame<>(aut);
		QuasiTimeEmptinessCheckV2<RankedBool, ParityState<String>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
}

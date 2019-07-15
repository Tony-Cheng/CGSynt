package test.tree.parity.operation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import cgsynt.tree.parity.operations.ParityEmptinessCheck;
import test.tree.buchi.RankedLetter;

public class EmptinessCheckTest {

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

		ParityEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new ParityEmptinessCheck<>(aut);
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

		ParityEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new ParityEmptinessCheck<>(aut);
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

		ParityEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new ParityEmptinessCheck<>(aut);
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

		ParityEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new ParityEmptinessCheck<>(aut);
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

		ParityEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new ParityEmptinessCheck<>(aut);
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

		ParityEmptinessCheck<RankedBool, ParityState<String>> emptinessCheck = new ParityEmptinessCheck<>(aut);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
}

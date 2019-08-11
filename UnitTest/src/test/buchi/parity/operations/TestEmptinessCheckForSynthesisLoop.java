package test.buchi.parity.operations;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.operations.BuchiParityEmptinessCheck;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

public class TestEmptinessCheckForSynthesisLoop {

	@Test
	void test1() {
		RankedBool.setRank(2);
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut = new ParityTreeAutomaton<>(2);
		ParityState<String> c0 = new ParityState<String>("c0", 2);
		ParityState<String> d0 = new ParityState<String>("d0", 1);
		ParityState<String> e0 = new ParityState<String>("e0", 1);
		ParityState<String> b0 = new ParityState<String>("b0", 0);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(d0);
		list1.add(d0);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(e0);
		list2.add(e0);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(b0);
		list3.add(b0);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				c0, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				d0, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3 = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				e0, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule4 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				e0, list3);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule5 = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				b0, list3);

		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addRule(rule4);
		aut.addRule(rule5);
		aut.addInitState(c0);

		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		String bs1 = "buchi state 1";
		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs1);
		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, bs1, blist1);
		aut1.addRule(brule1);
		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut1, aut);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> emptinessCheck = new BuchiParityEmptinessCheck<>(
				aut3);
		emptinessCheck.computeResult();
		assertTrue(emptinessCheck.getResult());
	}
	
	@Test
	void test2() {
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<RankedBool, String>(2);
		String b0 = "b0";
		String c0 = "c0";
		String d0 = "d0";
		String e0 = "e0";
		
		List<String> dest1 = new ArrayList<>();
		dest1.add(c0);
		dest1.add(d0);
		
		List<String> dest2 = new ArrayList<>();
		dest2.add(c0);
		dest2.add(e0);
		
		List<String> dest3 = new ArrayList<>();
		dest3.add(e0);
		dest3.add(e0);
		
		List<String> dest4 = new ArrayList<>();
		dest4.add(e0);
		dest4.add(e0);
		
		aut.addInitState(b0);
		aut.addFinalState(b0);
		aut.addFinalState(c0);
		aut.addFinalState(d0);
		aut.addFinalState(e0);
		
		BuchiTreeAutomatonRule<RankedBool, String> rule1 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, b0, dest1);
		BuchiTreeAutomatonRule<RankedBool, String> rule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, c0, dest2);
		BuchiTreeAutomatonRule<RankedBool, String> rule3 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, d0, dest3);
		BuchiTreeAutomatonRule<RankedBool, String> rule4 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, e0, dest4);
		BuchiTreeAutomatonRule<RankedBool, String> rule5 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, e0, dest4);
		
		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addRule(rule4);
		aut.addRule(rule5);


		
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				RankedBool.TRUE, q0, list);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				RankedBool.FALSE, q0, list);

		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertFalse(empty.getResult());
	}
	
	@Test
	void test3() {
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<RankedBool, String>(2);
		String b0 = "b0";
		String c0 = "c0";
		String d0 = "d0";
		String e0 = "e0";
		
		List<String> dest1 = new ArrayList<>();
		dest1.add(c0);
		dest1.add(d0);
		
		List<String> dest2 = new ArrayList<>();
		dest2.add(c0);
		dest2.add(e0);
		
		List<String> dest3 = new ArrayList<>();
		dest3.add(e0);
		dest3.add(e0);
		
		List<String> dest4 = new ArrayList<>();
		dest4.add(e0);
		dest4.add(e0);
		
		aut.addInitState(b0);
		aut.addFinalState(b0);
		aut.addFinalState(c0);
		aut.addFinalState(d0);
		aut.addFinalState(e0);
		
		BuchiTreeAutomatonRule<RankedBool, String> rule1 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, b0, dest1);
		BuchiTreeAutomatonRule<RankedBool, String> rule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, c0, dest2);
		BuchiTreeAutomatonRule<RankedBool, String> rule3 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, d0, dest3);
		BuchiTreeAutomatonRule<RankedBool, String> rule4 = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, e0, dest4);
		BuchiTreeAutomatonRule<RankedBool, String> rule5 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, e0, dest4);
		
		aut.addRule(rule1);
		aut.addRule(rule2);
		aut.addRule(rule3);
		aut.addRule(rule4);
		aut.addRule(rule5);


		
		ParityTreeAutomaton<RankedBool, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> c0x = new ParityState<String>("c0x", 2);
		ParityState<String> d0x = new ParityState<String>("d0x", 1);
		ParityState<String> e0x = new ParityState<String>("e0x", 1);
		ParityState<String> b0x = new ParityState<String>("b0x", 0);

		List<ParityState<String>> list1 = new ArrayList<>();
		list1.add(d0x);
		list1.add(d0x);

		List<ParityState<String>> list2 = new ArrayList<>();
		list2.add(e0x);
		list2.add(e0x);

		List<ParityState<String>> list3 = new ArrayList<>();
		list3.add(b0x);
		list3.add(b0x);

		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule1x = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				c0x, list1);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule2x = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				d0x, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule3x = new ParityTreeAutomatonRule<>(RankedBool.FALSE,
				e0x, list2);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule4x = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				e0x, list3);
		ParityTreeAutomatonRule<RankedBool, ParityState<String>> rule5x = new ParityTreeAutomatonRule<>(RankedBool.TRUE,
				b0x, list3);

		aut2.addRule(rule1x);
		aut2.addRule(rule2x);
		aut2.addRule(rule3x);
		aut2.addRule(rule4x);
		aut2.addRule(rule5x);
		aut2.addInitState(c0x);

		BuchiParityIntersectAutomaton<RankedBool, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut, aut2);

		BuchiParityEmptinessCheck<RankedBool, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertTrue(empty.getResult());
	}
}

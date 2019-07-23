package test.buchi.parity.operations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.EmptinessCheck;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.operations.BuchiParityEmptinessCheck;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

public class TestBuchiEmptinessCheck {

	private static BuchiTreeAutomaton<RankedLetter, String> emptyParameterized;
	private static BuchiTreeAutomaton<RankedLetter, String> nonEmptyParameterized;
	private static BuchiTreeAutomaton<RankedLetter, String> nonEmptyParameterizedWithRandom;

	@BeforeAll
	static void init() {
		emptyParameterized = BuchiTreeAutomatonFactory.parameterizedEmpty(19); // 2^19 - 1 states
		nonEmptyParameterized = BuchiTreeAutomatonFactory.parameterizedNonEmpty(19); // 2^19 - 1 states
		nonEmptyParameterizedWithRandom = BuchiTreeAutomatonFactory.parameterizedNonEmptyWithRandom(15, 1000);
	}

	@Test
	void testSingleNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();
		
		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);

		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				single, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertFalse(empty.getResult());
	}

	@Test
	void testDoubleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.doubleEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);
		empty.computeResult();
		assertTrue(empty.getResult());

	}

	@Test
	void testComplexNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexNonEmpty();

		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);

		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertFalse(empty.getResult());

	}

	@Test
	void testComplexEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexEmpty();

		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);

		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertTrue(empty.getResult());

	}

	@Test
	void testOrderNonempty() {
		BuchiTreeAutomaton<RankedLetter, String> machine = BuchiTreeAutomatonFactory.orderNonEmpty();

		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);
		
		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				machine, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertFalse(empty.getResult());
	}

	@Test
	void testAnthonyEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.emptyBuchiTree();

		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);
		
		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertTrue(empty.getResult());

	}

	@Test
	void testAnthonyNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.nonEmptyBuchiTree();

		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);
		
		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				aut, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertFalse(empty.getResult());
	}

//	@Test
//	void testParameterizedEmpty() {
//		BuchiTreeAutomaton<RankedLetter, String> aut = emptyParameterized;
//
//		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
//		ParityState<String> q0 = new ParityState<String>("q0", 0);
//		List<ParityState<String>> list = new ArrayList<>();
//		list.add(q0);
//		list.add(q0);
//		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
//				BuchiTreeAutomatonFactory.LETTERA, q0, list);
//		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
//				BuchiTreeAutomatonFactory.LETTERB, q0, list);
//		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
//				BuchiTreeAutomatonFactory.LETTERC, q0, list);
//		
//		aut2.addRule(prule1);
//		aut2.addRule(prule2);
//		aut2.addRule(prule3);
//		aut2.addInitState(q0);
//
//		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
//				aut, aut2);
//
//		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
//				aut3);
//		empty.computeResult();
//		assertTrue(empty.getResult());
//	}

//	@Test
//	void testParameterizedNoneEmpty() {
//		BuchiTreeAutomaton<RankedLetter, String> aut = nonEmptyParameterized;
//
//		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
//		ParityState<String> q0 = new ParityState<String>("q0", 0);
//		List<ParityState<String>> list = new ArrayList<>();
//		list.add(q0);
//		list.add(q0);
//		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
//				BuchiTreeAutomatonFactory.LETTERA, q0, list);
//		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
//				BuchiTreeAutomatonFactory.LETTERB, q0, list);
//		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
//				BuchiTreeAutomatonFactory.LETTERC, q0, list);
//		
//		aut2.addRule(prule1);
//		aut2.addRule(prule2);
//		aut2.addRule(prule3);
//		aut2.addInitState(q0);
//
//		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
//				aut, aut2);
//
//		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
//				aut3);
//		empty.computeResult();
//		assertFalse(empty.getResult());
//	}

	@Test
	void testParameterizedNonEmptyWithRandom() {
		BuchiTreeAutomaton<RankedLetter, String> machine = nonEmptyParameterizedWithRandom;

		ParityTreeAutomaton<RankedLetter, ParityState<String>> aut2 = new ParityTreeAutomaton<>(2);
		ParityState<String> q0 = new ParityState<String>("q0", 0);
		List<ParityState<String>> list = new ArrayList<>();
		list.add(q0);
		list.add(q0);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule1 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERA, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule2 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERB, q0, list);
		ParityTreeAutomatonRule<RankedLetter, ParityState<String>> prule3 = new ParityTreeAutomatonRule<>(
				BuchiTreeAutomatonFactory.LETTERC, q0, list);
		
		aut2.addRule(prule1);
		aut2.addRule(prule2);
		aut2.addRule(prule3);
		aut2.addInitState(q0);

		BuchiParityIntersectAutomaton<RankedLetter, String, ParityState<String>> aut3 = new BuchiParityIntersectAutomaton<>(
				machine, aut2);

		BuchiParityEmptinessCheck<RankedLetter, String, ParityState<String>> empty = new BuchiParityEmptinessCheck<>(
				aut3);
		empty.computeResult();
		assertFalse(empty.getResult());
	}

}

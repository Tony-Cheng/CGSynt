package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.operations.EmptinessCheck;

class EmptinessCheckTest {

	private static BuchiTreeAutomaton<RankedLetter, String> emptyParameterized;
	private static BuchiTreeAutomaton<RankedLetter, String> nonEmptyParameterized;
	private static BuchiTreeAutomaton<RankedLetter, String> nonEmptyParameterizedWithRandom;
	
	@BeforeAll
	static void init() {
		emptyParameterized = BuchiTreeAutomatonFactory.parameterizedEmpty(19);				// 2^19 - 1 states
		nonEmptyParameterized = BuchiTreeAutomatonFactory.parameterizedNonEmpty(19);		// 2^19 - 1 states
		nonEmptyParameterizedWithRandom = BuchiTreeAutomatonFactory.parameterizedNonEmptyWithRandom(15, 1000);
	}
	
	@Test
	void testSingleNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(single);

		assertFalse(empty.computeResult());
	}

	@Test
	void testDoubleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.doubleEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertTrue(empty.computeResult());

	}

	@Test
	void testComplexNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexNonEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertFalse(empty.computeResult());

	}

	@Test
	void testComplexEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertTrue(empty.computeResult());

	}

	@Test
	void testOrderNonempty() {
		BuchiTreeAutomaton<RankedLetter, String> machine = BuchiTreeAutomatonFactory.orderNonEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(machine);

		assertFalse(empty.computeResult());
	}

	@Test
	void testAnthonyEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.anthonyEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertTrue(empty.computeResult());

	}

	@Test
	void testAnthonyNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.anthonyNonEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertFalse(empty.computeResult());
	}
	
	@Test
	void testParameterizedEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = emptyParameterized;

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertTrue(empty.computeResult());
	}
	
	@Test
	void testParameterizedNoneEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = nonEmptyParameterized;

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertFalse(empty.computeResult());
	}
	
	@Test
	void testParameterizedNonEmptyWithRandom() {
		BuchiTreeAutomaton<RankedLetter, String> machine = nonEmptyParameterizedWithRandom;
		
		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(machine);
		
		assertFalse(empty.computeResult());
	}
}

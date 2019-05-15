package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;
import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.operations.EmptinessCheck;

class EmptinessCheckTest {

	@Test
	void testSingleNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(single);

		assertFalse(empty.getResult());
	}

	@Test
	void testDoubleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.doubleEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertTrue(empty.getResult());

	}

	@Test
	void testComplexNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexNonEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertFalse(empty.getResult());

	}
	
	@Test 
	void testComplexEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexEmpty();

		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);

		assertTrue(empty.getResult());

	}

}

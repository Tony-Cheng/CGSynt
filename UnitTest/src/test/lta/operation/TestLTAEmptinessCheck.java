package test.lta.operation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.operations.LTAEmptinessCheck;
import test.tree.buchi.RankedLetter;
import test.tree.lta.LTAFactory;

public class TestLTAEmptinessCheck {

	@Test
	void singleNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.singleNonEmpty();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertFalse(isEmpty.getResult());
	}

	@Test
	void singleEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.singleEmpty();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertTrue(isEmpty.getResult());
	}
	
	@Test
	void complexNonEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.complexNonEmpty();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertFalse(isEmpty.getResult());
	}
	
	@Test
	void complexEmpty() {
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.complexEmpty();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertTrue(isEmpty.getResult());
	}
}

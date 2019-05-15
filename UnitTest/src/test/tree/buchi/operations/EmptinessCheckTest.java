package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;
import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.operations.EmptinessCheck;

class EmptinessCheckTest {

	@Test
	void test() {
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();
		
		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(single);
		
		assertFalse(empty.getResult());
	}

}

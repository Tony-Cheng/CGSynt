package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.IntersectState;
import usra.tree.buchi.operations.BuchiIntersectTree;

class BuchiIntersectTreeTest {

	@Test
	void testOneTwoIntersect() {
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> doubleAut = BuchiTreeAutomatonFactory.doubleEmpty();

		BuchiIntersectTree<RankedLetter, String> intersect = new BuchiIntersectTree<>(single, doubleAut);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String>> intersectedState = intersect.computeResult();

		assertEquals(intersectedState.getFinalStates().size(), 1);
		assertTrue(intersectedState.getFinalStates().contains(new IntersectState<String>("q0", "q1", 2)));
		assertEquals(intersectedState.getAmountOfRules(), 4);
		assertEquals(intersectedState.getInitStates().size(), 1);
		assertTrue(intersectedState.getInitStates().contains(new IntersectState<String>("q0", "q0", 1)));
		assertTrue(intersectedState.getRank() == 2);

	}
}

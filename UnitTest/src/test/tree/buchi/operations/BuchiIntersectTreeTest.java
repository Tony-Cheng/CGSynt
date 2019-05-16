package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

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
		
		
	}
}

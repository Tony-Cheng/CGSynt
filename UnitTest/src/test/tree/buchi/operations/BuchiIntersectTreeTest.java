package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.operations.BuchiIntersectTree;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

class BuchiIntersectTreeTest {

	@Test
	void test2() {
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
		System.out.println("Test2");
		System.out.println(intersectedState);

	}
	
	@Test
	void test1() {
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.single();
		BuchiIntersectTree<RankedLetter, String> intersect = new BuchiIntersectTree<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String>> intersectAut = intersect.computeResult();
		assertEquals(intersectAut.getFinalStates().size(), 1);
		assertEquals(intersectAut.getStates().size(), 2);
		System.out.println("Test1\n");
		System.out.println(intersectAut);
	}
}

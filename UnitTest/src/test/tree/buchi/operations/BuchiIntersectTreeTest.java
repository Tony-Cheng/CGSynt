package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.operations.Intersect;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

class BuchiIntersectTreeTest {

	@Test
	void test2() {
		// Test intersecting an automaton with multiple transitions.
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> doubleAut = BuchiTreeAutomatonFactory.doubleEmpty();

		Intersect<RankedLetter, String> intersect = new Intersect<>(single, doubleAut);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String>> intersectedState = intersect.computeResult();

		assertEquals(intersectedState.getFinalStates().size(), 1);
		assertTrue(intersectedState.getFinalStates().contains(new IntersectState<String>("q0", "q1", 2)));
		assertEquals(intersectedState.getAmountOfRules(), 4);
		assertEquals(intersectedState.getInitStates().size(), 1);
		assertTrue(intersectedState.getInitStates().contains(new IntersectState<String>("q0", "q0", 1)));
		assertTrue(intersectedState.getRank() == 2);
		System.out.println("Test 2");
		System.out.println(intersectedState);

	}

	@Test
	void test1() {
		// Test intersecting two single state buchi tree automaton.
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.single();
		Intersect<RankedLetter, String> intersect = new Intersect<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String>> intersectAut = intersect.computeResult();
		assertEquals(intersectAut.getFinalStates().size(), 1);
		assertEquals(intersectAut.getStates().size(), 2);
		System.out.println("Test 1");
		System.out.println(intersectAut);
	}

	@Test
	void test3() {
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.SingleMultiLetter();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.SingleMultiLetter();
		Intersect<RankedLetter, String> intersect = new Intersect<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String>> intersectAut = intersect.computeResult();
		System.out.println("Test 3");
		System.out.println(intersectAut);
	}
}

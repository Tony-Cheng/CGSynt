package test.tree.buchi.operations;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.operations.BuchiIntersection;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

class IntersectionTest {

	@Test
	void test2() {
		// Test intersecting an automaton with multiple transitions.
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> doubleAut = BuchiTreeAutomatonFactory.doubleEmpty();

		BuchiIntersection<RankedLetter, String, String> intersect = new BuchiIntersection<>(single, doubleAut);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String, String>> intersectedState = intersect.computeResult();

		assertEquals(intersectedState.getFinalStates().size(), 1);
		assertTrue(intersectedState.getFinalStates().contains(new IntersectState<String, String>("q0", "q1", 2)));
		assertEquals(intersectedState.getAmountOfRules(), 4);
		assertEquals(intersectedState.getInitStates().size(), 1);
		assertTrue(intersectedState.getInitStates().contains(new IntersectState<String, String>("q0", "q0", 1)));
		assertTrue(intersectedState.getRank() == 2);
		System.out.println("Test 2");
		System.out.println(intersectedState);

	}

	
	@Test
	void test1() {
		// Test intersecting two single state buchi tree automaton.
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.single();
		BuchiIntersection<RankedLetter, String, String> intersect = new BuchiIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String, String>> intersectAut = intersect.computeResult();
		assertEquals(intersectAut.getFinalStates().size(), 1);
		assertEquals(intersectAut.getStates().size(), 2);
		System.out.println("Test 1");
		System.out.println(intersectAut);
	}

	@Test
	void test3() {
		// Test intersecting with multiple letters in the alphabet
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.SingleMultiLetter();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.SingleMultiLetter();
		BuchiIntersection<RankedLetter, String, String> intersect = new BuchiIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String, String>> intersectAut = intersect.computeResult();
		System.out.println("Test 3");
		System.out.println(intersectAut);
	}

	@Test
	void test4() {
		// Test intersecting automatons with multiple initial states.
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.MultiInitStates1();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.MultiInitStates2();
		BuchiIntersection<RankedLetter, String, String> intersect = new BuchiIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String, String>> intersectAut = intersect.computeResult();
		System.out.println("Test 4");
		System.out.println(intersectAut);
	}
	
	@Test
	void test5() {
		// Test intersecting automatons with multiple final states
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.MultiFinalStates1();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.MultiFinalStates2();
		BuchiIntersection<RankedLetter, String, String> intersect = new BuchiIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, IntersectState<String, String>> intersectAut = intersect.computeResult();
		System.out.println("Test 5");
		System.out.println(intersectAut);
	}
}

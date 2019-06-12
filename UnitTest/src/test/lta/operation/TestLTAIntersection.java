package test.lta.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import cgsynt.tree.buchi.operations.Intersect;
import cgsynt.tree.buchi.operations.LTAIntersection;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

/**
 * TestLTAIntersection use the same test cases as IntersectionTest.
 */
public class TestLTAIntersection {

	@Test
	void test2() {
		// Test intersecting an automaton with multiple transitions.
		BuchiTreeAutomaton<RankedLetter, String> single = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> doubleAut = BuchiTreeAutomatonFactory.doubleEmpty();
		single.setAllStatesFinal();
		doubleAut.setAllStatesFinal();
		
		LTAIntersection<RankedLetter, String> intersect = new LTAIntersection<>(single, doubleAut);
		BuchiTreeAutomaton<RankedLetter, LTAIntersectState<String>> intersectedState = intersect.computeResult();

		assertEquals(intersectedState.getFinalStates().size(), 2);
		assertTrue(intersectedState.getFinalStates().contains(new LTAIntersectState<String>("q0", "q1")));
		assertEquals(intersectedState.getAmountOfRules(), 2);
		assertEquals(intersectedState.getInitStates().size(), 1);
		assertTrue(intersectedState.getInitStates().contains(new LTAIntersectState<String>("q0", "q0")));
		assertTrue(intersectedState.getRank() == 2);
		System.out.println("Test 2");
		System.out.println(intersectedState);

	}

	@Test
	void test1() {
		// Test intersecting two single state buchi tree automaton.
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.single();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.single();
		aut1.setAllStatesFinal();
		aut2.setAllStatesFinal();
		LTAIntersection<RankedLetter, String> intersect = new LTAIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, LTAIntersectState<String>> intersectAut = intersect.computeResult();
		assertEquals(intersectAut.getFinalStates().size(), 1);
		assertEquals(intersectAut.getStates().size(), 1);
		System.out.println("Test 1");
		System.out.println(intersectAut);
	}

	@Test
	void test3() {
		// Test intersecting with multiple letters in the alphabet
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.SingleMultiLetter();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.SingleMultiLetter();
		aut1.setAllStatesFinal();
		aut2.setAllStatesFinal();
		LTAIntersection<RankedLetter, String> intersect = new LTAIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, LTAIntersectState<String>> intersectAut = intersect.computeResult();
		System.out.println("Test 3");
		System.out.println(intersectAut);
	}

	@Test
	void test4() {
		// Test intersecting automatons with multiple initial states.
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.MultiInitStates1();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.MultiInitStates2();
		aut1.setAllStatesFinal();
		aut2.setAllStatesFinal();
		LTAIntersection<RankedLetter, String> intersect = new LTAIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, LTAIntersectState<String>> intersectAut = intersect.computeResult();
		System.out.println("Test 4");
		System.out.println(intersectAut);
	}
	
	@Test
	void test5() {
		// Test intersecting automatons with multiple final states
		BuchiTreeAutomaton<RankedLetter, String> aut1 = BuchiTreeAutomatonFactory.MultiFinalStates1();
		BuchiTreeAutomaton<RankedLetter, String> aut2 = BuchiTreeAutomatonFactory.MultiFinalStates2();
		aut1.setAllStatesFinal();
		aut2.setAllStatesFinal();
		LTAIntersection<RankedLetter, String> intersect = new LTAIntersection<>(aut1, aut2);
		BuchiTreeAutomaton<RankedLetter, LTAIntersectState<String>> intersectAut = intersect.computeResult();
		System.out.println("Test 5");
		System.out.println(intersectAut);
	}

}

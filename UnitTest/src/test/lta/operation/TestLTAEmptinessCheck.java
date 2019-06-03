package test.lta.operation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	@Test
	void test1() {
		// Single letter single leaf
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.singleLeaf();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertTrue(isEmpty.getResult());
		System.out.println("Test 1 counterexamples:");
		printCounterExamples(generateCounterExamples(isEmpty));
	}
	
	@Test
	void test2() {
		// Nonempty NLTA
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.nonEmptyNLTA();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertFalse(isEmpty.getResult());
	}
	
	@Test
	void test3() {
		// MultiLetter nonempty LTA
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.nonEmptyMultiLetterLTA();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertFalse(isEmpty.getResult());
	}
	
	@Test
	void test4() {
		// MultiLetter empty LTA
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.emptyMultiLetterLTA();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertTrue(isEmpty.getResult());
		System.out.println("Test 4 counterexamples:");
		printCounterExamples(generateCounterExamples(isEmpty));
	}
	
	@Test
	void test5() {
		// Empty NLTA
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.emptyNLTA();
		LTAEmptinessCheck<RankedLetter, String> isEmpty = new LTAEmptinessCheck<>(aut);
		isEmpty.computeResult();
		assertTrue(isEmpty.getResult());
		System.out.println("Test 5 counterexamples:");
		printCounterExamples(generateCounterExamples(isEmpty));
	}

	private List<String> generateDefaultLetters() {
		List<String> letters = new ArrayList<String>();
		letters.add("a");
		letters.add("b");
		return letters;
	}

	private Set<List<String>> generateCounterExamples(LTAEmptinessCheck<RankedLetter, String> isEmpty) {
		isEmpty.computeResult();
		return isEmpty.findCounterExamples(generateDefaultLetters());
	}

	private void printCounterExamples(Set<List<String>> counterExamples) {
		for (List<String> counterExample : counterExamples) {
			for (int i = 0; i < counterExample.size(); i++) {
				System.out.print(counterExample.get(i));
			}
			System.out.println();
		}
		System.out.println();
	}
}

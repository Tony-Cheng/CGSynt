package test.tree.buchi.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.operations.EmptinessCheck;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

public class CounterExampleTest {

	@Test
	void testSingleCounterExample() {
		System.out.println("testSingleCounterExample");
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.LTATripleEmpty();
		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);
		empty.computeResult();
		List<String> alphabet = new ArrayList<>();
		alphabet.add("a");
		alphabet.add("b");
		Set<List<String>> counterExamples = empty.findCounterExamples(alphabet);
		Set<List<String>> expectedResult = new HashSet<>();
		List<String> expectedResult1 = new ArrayList<>();
		expectedResult1.add("a");
		expectedResult1.add("b");
		List<String> expectedResult2 = new ArrayList<>();
		expectedResult2.add("b");
		expectedResult.add(expectedResult1);
		expectedResult.add(expectedResult2);
		expectedResult.containsAll(counterExamples);
		for (List<String> word : counterExamples) {
			for (String letter : word) {
				System.out.print(letter);
			}
			System.out.println();
		}
		System.out.println();
	}

	@Test
	void testMultiCounterExamples() {
		System.out.println("testMultiCounterExamples");
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.LTAMultiLetter();
		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);
		empty.computeResult();
		List<String> alphabet = new ArrayList<>();
		alphabet.add("a");
		alphabet.add("b");
		Set<List<String>> counterExamples = empty.findCounterExamples(alphabet);

		for (List<String> word : counterExamples) {
			for (String letter : word) {
				System.out.print(letter);
			}
			System.out.println();
		}
		System.out.println();
	}

	@Test
	void testSingleCounterExample2() {
		System.out.println("testSingleCounterExample2");
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.EmptyTreeLTA();
		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);
		empty.computeResult();
		List<String> alphabet = new ArrayList<>();
		alphabet.add("a");
		alphabet.add("b");
		Set<List<String>> counterExamples = empty.findCounterExamples(alphabet);

		for (List<String> word : counterExamples) {
			for (String letter : word) {
				System.out.print(letter);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	@Test
	void testMultiInitCounterExample() {
		System.out.println("testMultiInitCounterExample");
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.complexEmptyLTA();
		EmptinessCheck<RankedLetter, String> empty = new EmptinessCheck<>(aut);
		empty.computeResult();
		List<String> alphabet = new ArrayList<>();
		alphabet.add("a");
		alphabet.add("b");
		Set<List<String>> counterExamples = empty.findCounterExamples(alphabet);

		for (List<String> word : counterExamples) {
			for (String letter : word) {
				System.out.print(letter);
			}
			System.out.println();
		}
		System.out.println();
	}

}

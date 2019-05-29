package test.lta.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;
import test.tree.lta.LTAFactory;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.operations.EmptinessCheck;

public class TestFindingCounterExample {

	@Test
	void simple() {
		System.out.println("simple");
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.singleEmpty();
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

		assertEquals(counterExamples.size(), 1);
	}

	@Test
	void complex() {
		System.out.println("complex");
		BuchiTreeAutomaton<RankedLetter, String> aut = LTAFactory.complexEmpty();
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

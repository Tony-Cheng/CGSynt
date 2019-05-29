package test.tree.buchi.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.operations.EmptinessCheck;

public class CounterExampleTest {
	
	@Test
	void test1() {
		BuchiTreeAutomaton<RankedLetter, String> aut = BuchiTreeAutomatonFactory.LTATripleEmpty();
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
	}

}

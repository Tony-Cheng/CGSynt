package test.tree.buchi.operations;

import org.junit.jupiter.api.Test;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.operations.BuchiTreeToParityTree;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import test.tree.buchi.BuchiTreeAutomatonFactory;
import test.tree.buchi.RankedLetter;

public class TestBuchiTreeToParityTree {
	
	@Test
	public void test1() {
		BuchiTreeAutomaton<RankedLetter, String> buchiTree = BuchiTreeAutomatonFactory.doubleEmpty();
		
		BuchiTreeToParityTree<RankedLetter, String> converter = 
				new BuchiTreeToParityTree<>(buchiTree);
		
		ParityTreeAutomaton<RankedLetter, ParityState<String>> parityTree = converter.getResult();
		
		System.out.println(parityTree);
	}
	
	@Test
	public void test2() {
		BuchiTreeAutomaton<RankedLetter, String> buchiTree = BuchiTreeAutomatonFactory.LTAMultiLetter();
		
		BuchiTreeToParityTree<RankedLetter, String> converter =
				new BuchiTreeToParityTree<>(buchiTree);
		
		ParityTreeAutomaton<RankedLetter, ParityState<String>> parityTree = converter.getResult();
		
		System.out.println(parityTree);
	}
	
	@Test
	public void test3() {
		BuchiTreeAutomaton<RankedLetter, String> buchiTree = BuchiTreeAutomatonFactory.orderNonEmpty();
		
		BuchiTreeToParityTree<RankedLetter, String> converter =
				new BuchiTreeToParityTree<>(buchiTree);
		
		ParityTreeAutomaton<RankedLetter, ParityState<String>> parityTree = converter.getResult();
		
		System.out.println(parityTree);
	}
}

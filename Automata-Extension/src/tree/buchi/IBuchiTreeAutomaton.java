package tree.buchi;

import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * @author weiszben
 *
 * @param <LETTER>
 * 			is the type of alphabet
 * @param <STATE>
 * 			is the type of state
 */
public interface IBuchiTreeAutomaton<LETTER extends IRankedLetter, STATE> extends IAutomaton<LETTER, STATE>{
	
	
	void addRule(final BuchiTreeAutomatonRule<LETTER, STATE> rule);
	
}

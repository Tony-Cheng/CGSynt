package tree.buchi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.automata.tree.ITreeAutomatonBU;
import de.uni_freiburg.informatik.ultimate.automata.tree.TreeAutomatonRule;

/**
 * 
 * @author
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class BuchiTreeAutomaton
<LETTER extends IRankedLetter, STATE> implements IBuchiTreeAutomaton<LETTER, STATE> {
	private final Set<LETTER> mAlphabet;
	private final Map<List<STATE>, Map<LETTER, Collection<TreeAutomatonRule<LETTER, STATE>>>> mChildrenMap;
	private final Set<STATE> mFinalStates;
	private final Map<LETTER, Collection<TreeAutomatonRule<LETTER, STATE>>> mLettersMap;
	private final Map<STATE, Map<LETTER, Collection<TreeAutomatonRule<LETTER, STATE>>>> mParentsMap;
	private final Set<TreeAutomatonRule<LETTER, STATE>> mRules;
	private final Map<STATE, Collection<TreeAutomatonRule<LETTER, STATE>>> mSourceMap;
	private final Set<STATE> mStates;
	
	/**
	 * Create a BuchiTreeAutomaton.
	 */
	public BuchiTreeAutomaton() {
		mChildrenMap = new HashMap<>();
		mParentsMap = new HashMap<>();
		mAlphabet = new HashSet<>();
		mLettersMap = new HashMap<>();
		mSourceMap = new HashMap<>();
		mRules = new HashSet<>();
		mFinalStates = new HashSet<>();
		mStates = new HashSet<>();
	}
	
}

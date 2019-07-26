package cgsynt.synthesis;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.statefactory.IDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class PDeterminizeStateFactory implements IDeterminizeStateFactory<IPredicate>{

	private BasicPredicateFactory mPredicateFactory;
	
	public PDeterminizeStateFactory(BasicPredicateFactory predicateFactory) {
		mPredicateFactory = predicateFactory;
	}
	
	@Override
	public IPredicate createEmptyStackState() {
		return mPredicateFactory.newDebugPredicate("PDeterminizeStateFactory:emptyStackState");
	}

	@Override
	public IPredicate determinize(Map<IPredicate, Set<IPredicate>> down2up) {
		Set<Entry<IPredicate, Set<IPredicate>>> entries = down2up.entrySet();
		Set<IPredicate> preds = new HashSet<>();
		
		for (Entry<IPredicate, Set<IPredicate>> entry : entries) {
			if (!preds.contains(entry.getKey()))
				preds.add(entry.getKey());
			for (IPredicate pred : entry.getValue()) {
				if (!preds.contains(pred))
					preds.add(pred);
			}
		}
		
		return mPredicateFactory.or(false, preds);
	}

}

package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;

public interface IStatement {

	public NestedWord<IAction> getTrace();
}

package cgsynt.interpol;

import java.util.ArrayList;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.BasicInternalAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

public class ScriptAssignmentStatement implements IStatement {

	private BoogieNonOldVar lhs;
	private Term rhs;
	private DefaultIcfgSymbolTable symbolTable;
	private ManagedScript managedScript;

	/**
	 * Initialize this ScriptStatement object.
	 * 
	 * @param lhs
	 *            the left hand side of the statement.
	 * @param rhs
	 *            the right hand side of the statement.
	 */
	public ScriptAssignmentStatement(BoogieNonOldVar lhs, Term rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.symbolTable = TraceGlobalVariables.getGlobalVariables().getVariableFactory().getSymbolTable();
		this.managedScript = TraceGlobalVariables.getGlobalVariables().getManagedScript();
	}

	@Override
	public NestedWord<IAction> getTrace() {
		List<IProgramVar> lhs = new ArrayList<>();
		List<Term> rhs = new ArrayList<>();
		lhs.add(this.lhs);
		rhs.add(this.rhs);
		UnmodifiableTransFormula formula = TransFormulaBuilder.constructAssignment(lhs, rhs, symbolTable,
				managedScript);
		BasicInternalAction basicAction = new BasicInternalAction(null, null, formula);
		IAction[] word = new IAction[1];
		int[] nestingRelation = new int[1];
		word[0] = basicAction;
		nestingRelation[0] = NestedWord.INTERNAL_POSITION;
		NestedWord<IAction> trace = new NestedWord<>(word, nestingRelation);
		return trace;
	}

	@Override
	public IAction getFormula() {
		List<IProgramVar> lhs = new ArrayList<>();
		List<Term> rhs = new ArrayList<>();
		lhs.add(this.lhs);
		rhs.add(this.rhs);
		UnmodifiableTransFormula formula = TransFormulaBuilder.constructAssignment(lhs, rhs, symbolTable,
				managedScript);
		return new BasicInternalAction(null, null, formula);
	}

	@Override
	public String toString() {
		return lhs.toString() + " := " + rhs.toString();
	}
}

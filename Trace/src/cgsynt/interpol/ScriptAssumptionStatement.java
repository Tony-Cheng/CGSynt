package cgsynt.interpol;

import java.util.ArrayList;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.BasicInternalAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

public class ScriptAssumptionStatement implements IAssumption {

	private BoogieNonOldVar lhs;
	private Term rhs;
	private DefaultIcfgSymbolTable symbolTable;
	private ManagedScript managedScript;
	private String type;
	private boolean negated;

	public ScriptAssumptionStatement(BoogieNonOldVar lhs, Term rhs, String type) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.type = type;
		symbolTable = TraceGlobalVariables.getGlobalVariables().getVariableFactory().getSymbolTable();
		managedScript = TraceGlobalVariables.getGlobalVariables().getManagedScript();
		this.negated = false;

	}

	@Override
	public NestedWord<IAction> getTrace() {
		return getTraceInternal(false);
	}

	@Override
	public IAction getFormula() {
		if (negated)
			return getFormulaInternal(true);
		return getFormulaInternal(false);
	}

	public NestedWord<IAction> getNegatedTrace() {
		return getTraceInternal(true);
	}

	public IAction getNegatedFormula() {
		if (negated)
			return getFormulaInternal(false);
		return getFormulaInternal(true);
	}

	private NestedWord<IAction> getTraceInternal(boolean negated) {
		List<IProgramVar> lhs = new ArrayList<>();
		List<Term> rhs = new ArrayList<>();
		lhs.add(this.lhs);
		rhs.add(this.rhs);
		UnmodifiableTransFormula formula = ExtendedTransFormulaBuilder.constructAssumption(lhs, rhs, symbolTable,
				managedScript, type, negated);
		BasicInternalAction basicAction = new BasicInternalAction(null, null, formula);
		IAction[] word = new IAction[1];
		int[] nestingRelation = new int[1];
		word[0] = basicAction;
		nestingRelation[0] = NestedWord.INTERNAL_POSITION;
		NestedWord<IAction> trace = new NestedWord<>(word, nestingRelation);
		return trace;
	}

	private IAction getFormulaInternal(boolean negated) {
		List<IProgramVar> lhs = new ArrayList<>();
		List<Term> rhs = new ArrayList<>();
		lhs.add(this.lhs);
		rhs.add(this.rhs);
		UnmodifiableTransFormula formula = ExtendedTransFormulaBuilder.constructAssumption(lhs, rhs, symbolTable,
				managedScript, type, negated);
		return new BasicInternalAction(null, null, formula);
	}

	@Override
	public String toString() {
		return lhs.toString() + " " + type + " " + rhs.toString();
	}

	@Override
	public void negate() {
		negated = !negated;
	}

	@Override
	public boolean isNegated() {
		return negated;
	}

	@Override
	public boolean isAssumption() {
		return true;
	}

	@Override
	public IAssumption copy() {
		ScriptAssumptionStatement copy = new ScriptAssumptionStatement(lhs, rhs, type);
		copy.negated = negated;
		return copy;
	}
}

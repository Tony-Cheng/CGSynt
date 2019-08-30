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

/**
 * An assumption statement in the trace.
 *
 */
public class ScriptAssumptionStatement implements IAssumption {

	private BoogieNonOldVar lhs;
	private Term rhs;
	private DefaultIcfgSymbolTable symbolTable;
	private ManagedScript managedScript;
	private String type;
	private boolean negated;

	public ScriptAssumptionStatement(BoogieNonOldVar lhs, Term rhs, String type, ManagedScript managedScript,
			DefaultIcfgSymbolTable symbolTable) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.type = type;
		this.symbolTable = symbolTable;
		this.managedScript = managedScript;
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

	/**
	 * Return the negated trace that consists of only the statement that this object
	 * represents.
	 * 
	 * @return
	 */
	public NestedWord<IAction> getNegatedTrace() {
		return getTraceInternal(true);
	}

	/**
	 * Return the negated formula.
	 * @return
	 */
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
		if (negated)
			return "not " + lhs.toString() + " " + type + " " + rhs.toString();
		else
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
		ScriptAssumptionStatement copy = new ScriptAssumptionStatement(lhs, rhs, type, managedScript, symbolTable);
		copy.negated = negated;
		return copy;
	}

	@Override
	public UnmodifiableTransFormula getTransFormula(boolean negated) {
		List<IProgramVar> lhs = new ArrayList<>();
		List<Term> rhs = new ArrayList<>();
		lhs.add(this.lhs);
		rhs.add(this.rhs);
		UnmodifiableTransFormula formula = ExtendedTransFormulaBuilder.constructAssumption(lhs, rhs, symbolTable,
				managedScript, type, negated);
		return formula;
	}

	@Override
	public UnmodifiableTransFormula getTransFormula() {
		try {
			return getTransFormula(false);
			// throw new Exception();
		} catch (Exception e) {
			System.err.println(
					"ScriptAssumptionStatement.getTrasnFormula: For assumption statements you must use the version of the method that takes one parameter!");
			System.exit(1);
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		result = prime * result + (negated ? 1231 : 1237);
		result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScriptAssumptionStatement other = (ScriptAssumptionStatement) obj;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		if (negated != other.negated)
			return false;
		if (rhs == null) {
			if (other.rhs != null)
				return false;
		} else if (!rhs.equals(other.rhs))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}

package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.BasicInternalAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;

public class ScriptPredicateAssumptionStatement implements IAssumption {
	IPredicate predicate;
	private ManagedScript managedScript;
	private PredicateFactory predicateFactory;
	private boolean negated;

	public ScriptPredicateAssumptionStatement(IPredicate predicate) {
		this.predicate = predicate;
		managedScript = TraceGlobalVariables.getGlobalVariables().getManagedScript();
		negated = false;
	}

	@Override
	public NestedWord<IAction> getTrace() {
		UnmodifiableTransFormula formula = TransFormulaBuilder.constructTransFormulaFromPredicate(predicate,
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
		UnmodifiableTransFormula formula = TransFormulaBuilder.constructTransFormulaFromPredicate(predicate,
				managedScript);
		return new BasicInternalAction(null, null, formula);
	}

	@Override
	public void negate() {
		predicate = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory().not(predicate);
		negated = !negated;
	}

	@Override
	public boolean isNegated() {
		return negated;
	}

}

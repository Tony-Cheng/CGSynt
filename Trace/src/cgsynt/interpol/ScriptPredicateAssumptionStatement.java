package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.BasicInternalAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;

public class ScriptPredicateAssumptionStatement implements IAssumption {
	IPredicate predicate;
	private ManagedScript managedScript;
	private boolean negated;
	private BasicPredicateFactory predicateFactory;

	public ScriptPredicateAssumptionStatement(IPredicate predicate, ManagedScript managedScript,
			BasicPredicateFactory predicateFactory) {
		this.predicate = predicate;
		this.managedScript = managedScript;
		this.predicateFactory = predicateFactory;
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
		predicate = predicateFactory.not(predicate);
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
		ScriptPredicateAssumptionStatement copy = new ScriptPredicateAssumptionStatement(predicate, managedScript,
				predicateFactory);
		copy.negated = negated;
		return copy;
	}

	@Override
	public String toString() {
		return predicate.getFormula().toString();
	}
}

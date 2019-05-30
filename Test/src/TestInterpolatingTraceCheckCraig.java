import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.IIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.BasicInternalAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ProgramVarUtils;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtSortUtils;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUtils;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestInterpolatingTraceCheckCraig {

	private BoogieNonOldVar constructProgramVar(ManagedScript mMgdScript, final String identifier) {
		BoogieOldVar oldVar;
		final Sort sort = SmtSortUtils.getIntSort(mMgdScript);
		{
			final boolean isOldVar = true;
			final String name = ProgramVarUtils.buildBoogieVarName(identifier, null, true, isOldVar);
			final TermVariable termVariable = mMgdScript.variable(name, sort);
			final ApplicationTerm defaultConstant = ProgramVarUtils.constructDefaultConstant(mMgdScript, this, sort,
					name);
			final ApplicationTerm primedConstant = ProgramVarUtils.constructPrimedConstant(mMgdScript, this, sort,
					name);

			oldVar = new BoogieOldVar(identifier, null, termVariable, defaultConstant, primedConstant);
		}
		BoogieNonOldVar nonOldVar;
		{
			final boolean isOldVar = false;
			final String name = ProgramVarUtils.buildBoogieVarName(identifier, null, true, isOldVar);
			final TermVariable termVariable = mMgdScript.variable(name, sort);
			final ApplicationTerm defaultConstant = ProgramVarUtils.constructDefaultConstant(mMgdScript, this, sort,
					name);
			final ApplicationTerm primedConstant = ProgramVarUtils.constructPrimedConstant(mMgdScript, this, sort,
					name);

			nonOldVar = new BoogieNonOldVar(identifier, null, termVariable, defaultConstant, primedConstant, oldVar);
		}
		oldVar.setNonOldVar(nonOldVar);
		return nonOldVar;
	}

	public void TrestInterpolatingTraceCheckCraig() {
		final Script mScript = new SMTInterpol(new DefaultLogger());
		IUltimateServiceProvider mServices = UltimateMocks.createUltimateServiceProviderMock(LogLevel.INFO);
		ManagedScript mMgdScript = new ManagedScript(mServices, mScript);
		mScript.setOption(":produce-proofs", true);
		mScript.setLogic(Logics.QF_LIA);
		Set<IProgramVar> vars = new HashSet<>();
		BoogieNonOldVar a = constructProgramVar(mMgdScript, "a");
		Term preconditionFormula = mScript.term("=", a.getTermVariable(), mScript.numeral("1"));
		IIcfgSymbolTable symbolTable = new DefaultIcfgSymbolTable();
		// IPredicate precondition = new BasicPredicate(1, new String[0],
		// preconditionFormula, vars,
		// PredicateUtils.computeClosedFormula(preconditionFormula, vars, mScript));
		// IPredicate postcondition = new BasicPredicate(2, new String[0],
		// preconditionFormula, vars,
		// PredicateUtils.computeClosedFormula(preconditionFormula, vars, mScript));
		PredicateFactory factory = new PredicateFactory(mServices, mMgdScript, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		IPredicate precondition = factory.newEmptyStackPredicate();
		IPredicate postcondition = factory.newEmptyStackPredicate();
		NestedWord<IAction> trace = new NestedWord<>();
		BasicInternalAction addOne = new BasicInternalAction();
	}

	public static void main(String[] args) {
		new TestInterpolatingTraceCheckCraig();
	}

}

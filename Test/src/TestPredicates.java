import java.util.HashSet;
import java.util.Set;

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
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ProgramVarUtils;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtSortUtils;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.interpolant.IInterpolantGenerator;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUtils;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestPredicates {
	private ManagedScript mMgdScript;
	
	public TestPredicates() {
		basicPredicateTest();
	}
	
	public void basicPredicateTest() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		Script script = new SMTInterpol(new DefaultLogger());
		script.setLogic(Logics.QF_UFLIA);
		
		mMgdScript = new ManagedScript(mock, script);
		
		Set<IProgramVar> vars = new HashSet<>();
		BoogieNonOldVar x = constructProgramVar("x");
		BoogieNonOldVar y = constructProgramVar("y");
		
		vars.add(x);
		vars.add(y);
		
		Term formula = script.term(">", x.getTermVariable(), y.getTermVariable());
		Term closedFormula = PredicateUtils.computeClosedFormula(formula, vars, script);
		BasicPredicate pred = 
				new BasicPredicate(0, null, formula, vars, closedFormula);
		
		Term extractedTerm = pred.getClosedFormula();
		
		script.assertTerm(extractedTerm);
		script.checkSat();
	}
	
	public BoogieNonOldVar constructProgramVar(final String identifier) {
		BoogieOldVar oldVar;
		final Sort sort = SmtSortUtils.getIntSort(mMgdScript);
		{
			final boolean isOldVar = true;
			final String name = ProgramVarUtils.buildBoogieVarName(identifier, null, true, isOldVar);
			final TermVariable termVariable = mMgdScript.variable(name, sort);
			final ApplicationTerm defaultConstant =
					ProgramVarUtils.constructDefaultConstant(mMgdScript, this, sort, name);
			final ApplicationTerm primedConstant =
					ProgramVarUtils.constructPrimedConstant(mMgdScript, this, sort, name);

			oldVar = new BoogieOldVar(identifier, null, termVariable, defaultConstant, primedConstant);
		}
		BoogieNonOldVar nonOldVar;
		{
			final boolean isOldVar = false;
			final String name = ProgramVarUtils.buildBoogieVarName(identifier, null, true, isOldVar);
			final TermVariable termVariable = mMgdScript.variable(name, sort);
			final ApplicationTerm defaultConstant =
					ProgramVarUtils.constructDefaultConstant(mMgdScript, this, sort, name);
			final ApplicationTerm primedConstant =
					ProgramVarUtils.constructPrimedConstant(mMgdScript, this, sort, name);

			nonOldVar = new BoogieNonOldVar(identifier, null, termVariable, defaultConstant, primedConstant, oldVar);
		}
		oldVar.setNonOldVar(nonOldVar);
		return nonOldVar;
	}

	public static void main(String[] args) {
		new TestPredicates();
	}
}

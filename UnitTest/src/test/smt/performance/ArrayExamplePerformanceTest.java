package test.smt.performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import cgsynt.Verification.MainVerificationLoop;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.trace2.Assignment;
import cgsynt.trace2.Assumption;
import cgsynt.trace2.CraigInterpolant;
import cgsynt.trace2.Formula;
import cgsynt.trace2.Numerical;
import cgsynt.trace2.StandardFormula;
import cgsynt.trace2.Statement;
import cgsynt.trace2.Trace;
import cgsynt.trace2.TraceToCraigInterpolant;
import cgsynt.trace2.Variable;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.boogie.type.BoogieType;
import de.uni_freiburg.informatik.ultimate.core.model.models.IBoogieType;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ConcurrencyInformation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ModifiableGlobalsTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.SmtSymbols;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ThreadInstance;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.BasicInternalAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgForkTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgJoinTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ILocalProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;

public class ArrayExamplePerformanceTest {

	@Test
	void test1() throws Exception {
		MainVerificationLoop.resetAll();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
		BoogieNonOldVar m = vf.constructVariable("m", VariableFactory.INT);

		BoogieNonOldVar A = vf.constructVariable("A", VariableFactory.INT_ARR);
		IStatement iln = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IStatement mlai = new ScriptPredicateAssumptionStatement(predicateFactory
				.newPredicate(script.term("<", m.getTerm(), script.term("select", A.getTerm(), i.getTerm()))));
		IStatement mgeai = new ScriptPredicateAssumptionStatement(predicateFactory
				.newPredicate(script.term(">=", m.getTerm(), script.term("select", A.getTerm(), i.getTerm()))));
		IStatement meai = new ScriptAssignmentStatement(m, script.term("select", A.getTerm(), i.getTerm()));
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));

		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term(">=", n.getTerm(), script.numeral("1"))));
		preconditions = predicateFactory.and(preconditions, predicateFactory
				.newPredicate(script.term("=", m.getTerm(), script.term("select", A.getTerm(), script.numeral("0")))));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term(">=", j.getTerm(), script.numeral("0"))));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term("<", j.getTerm(), n.getTerm())));
		IPredicate postconditions = predicateFactory
				.newPredicate(script.term(">=", m.getTerm(), script.term("select", A.getTerm(), j.getTerm())));

		TraceToInterpolants.getTraceToInterpolants().setPreconditions(preconditions);
		TraceToInterpolants.getTraceToInterpolants().setPostconditions(postconditions);

		List<IStatement> trace = new ArrayList<>();

		trace.add(iln);
		trace.add(mgeai);
		trace.add(ipp);
		trace.add(iln);
		trace.add(mlai);
		trace.add(meai);
		trace.add(ipp);
		trace.add(igen);
		long time = System.nanoTime();
		int size = 1;
		for (int k = 0; k < size; k++) {
			TraceToInterpolants.getTraceToInterpolants().computeInterpolants(trace);
		}
		System.out.println("Average Time: " + (System.nanoTime() - time) / size / 1000000);

	}

	@Test
	void test2() {
		// Multi pre and post conditions using predicates
//		final Script s = new SMTInterpol(new DefaultLogger());
//		s.setOption(":produce-proofs", true);
//		s.setLogic(Logics.QF_ALIA);
//		Variable i0 = new Variable("i_0", new Sort[0], "Int");
//		Variable n = new Variable("n", new Sort[0], "Int");
//		Formula form1 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
//		Formula form2 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
//		Formula form3 = new StandardFormula("+", x, new Numerical("1"));
//		Formula form4 = new StandardFormula("=", x, new Numerical("-1"));
//		Statement s1 = new Assignment(x, form1);
//		Statement s2 = new Assignment(y, form2);
//		Statement s3 = new Assignment(x, form3);
//		Statement s4 = new Assumption(form4);
//
//		Trace trace = new Trace();
//		trace.addVariable(x);
//		trace.addVariable(y);
//
//		trace.addStatement(s1);
//		trace.addStatement(s2);
//		trace.addStatement(s3);
//		trace.addStatement(s4);
//
//		TraceToCraigInterpolant ttc = new TraceToCraigInterpolant(trace, s);
//
//		CraigInterpolant interpolants = ttc.computeResult();
	}
}

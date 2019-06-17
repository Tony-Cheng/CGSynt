package test.trace.to.interpolants;

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

import cgsynt.interpol.ExtendedTransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.boogie.type.BoogieType;
import de.uni_freiburg.informatik.ultimate.core.model.models.IBoogieType;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
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

public class TestCraigInterpolants4 {

	@Test
	void test1() {
		// Multi pre and post conditions using predicates
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		ILogger logger = new ConsoleLogger();
		ManagedScript managedScript = new ManagedScript(service, new SMTInterpol(new DefaultLogger()));
		Script script = managedScript.getScript();
		script.setOption(":produce-proofs", true);
		script.setLogic(Logics.QF_LIA);

		DefaultIcfgSymbolTable symbolTable = new DefaultIcfgSymbolTable();
		managedScript.getScript().declareFun("x", new Sort[0], managedScript.getScript().sort("Int"));
		managedScript.getScript().declareFun("xp", new Sort[0], managedScript.getScript().sort("Int"));
		managedScript.getScript().declareFun("y", new Sort[0], managedScript.getScript().sort("Int"));
		managedScript.getScript().declareFun("yp", new Sort[0], managedScript.getScript().sort("Int"));
		BoogieOldVar var1 = new BoogieOldVar("x", (IBoogieType) BoogieType.TYPE_INT,
				managedScript.getScript().variable("x", managedScript.getScript().sort("Int")),
				(ApplicationTerm) managedScript.getScript().term("x"),
				(ApplicationTerm) managedScript.getScript().term("xp"));
		BoogieNonOldVar var2 = new BoogieNonOldVar("xp", (IBoogieType) BoogieType.TYPE_INT,
				managedScript.getScript().variable("xp", managedScript.getScript().sort("Int")),
				(ApplicationTerm) managedScript.getScript().term("x"),
				(ApplicationTerm) managedScript.getScript().term("xp"), var1);
		var1.setNonOldVar(var2);
		symbolTable.add(var2);

		String procedure1 = "procedure1";
		String procedure2 = "procedure2";
		String procedure3 = "procedure3";
		String procedure4 = "procedure4";
		String procedure5 = "procedure5";
		HashRelation<String, IProgramNonOldVar> mProc2Globals = new HashRelation<>();
		ModifiableGlobalsTable modifiableGlobalsTable = new ModifiableGlobalsTable(mProc2Globals);
		Set<String> procedures = new HashSet<>();

		Map<String, List<ILocalProgramVar>> inParams = new HashMap<>();
		Map<String, List<ILocalProgramVar>> outParams = new HashMap<>();
		IcfgEdgeFactory icfgEdgeFactory = new IcfgEdgeFactory(new SerialProvider());
		Map<IIcfgForkTransitionThreadCurrent<IcfgLocation>, ThreadInstance> threadInstanceMap = new HashMap<>();
		Collection<IIcfgJoinTransitionThreadCurrent<IcfgLocation>> joinTransitions = new ArrayList<>();
		ConcurrencyInformation concurInfo = new ConcurrencyInformation(threadInstanceMap, joinTransitions);
		SmtSymbols smtSymbols = new SmtSymbols(managedScript.getScript());
		CfgSmtToolkit toolkit = new CfgSmtToolkit(modifiableGlobalsTable, managedScript, symbolTable, procedures,
				inParams, outParams, icfgEdgeFactory, concurInfo, smtSymbols);
		SortedMap<Integer, IPredicate> pendingContexts = new TreeMap<>();

		List<IProgramVar> lhs1 = new ArrayList<>();
		List<Term> rhs1 = new ArrayList<>();
		List<IProgramVar> lhs2 = new ArrayList<>();
		List<Term> rhs2 = new ArrayList<>();
		lhs1.add(var2);
		rhs1.add(script.numeral("1"));
		lhs2.add(var2);
		rhs2.add(script.numeral("9"));
		UnmodifiableTransFormula formula1 = ExtendedTransFormulaBuilder.constructEqualityAssumption(lhs1, rhs1, symbolTable,
				managedScript);
		UnmodifiableTransFormula formula2 = ExtendedTransFormulaBuilder.constructEqualityAssumption(lhs2, rhs2, symbolTable,
				managedScript);
		BasicInternalAction basicAction1 = new BasicInternalAction(null, null, formula1);
		BasicInternalAction basicAction2 = new BasicInternalAction(null, null, formula2);

		IAction[] word = new IAction[2];
		int[] nestingRelation = new int[2];
		word[0] = basicAction1;
		word[1] = basicAction2;
		nestingRelation[0] = NestedWord.INTERNAL_POSITION;
		nestingRelation[1] = NestedWord.INTERNAL_POSITION;

		NestedWord<IAction> trace = new NestedWord<>(word, nestingRelation);

		BasicPredicateFactory predicateFactory = new BasicPredicateFactory(service, managedScript, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		PredicateUnifier pUnifer = new PredicateUnifier(logger, service, managedScript, predicateFactory, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);

		Term x1 = script.term(">", var2.getTerm(), script.numeral("0"));
		Term x2 = script.term("=", var2.getTerm(), script.numeral("0"));
		IPredicate px1 = predicateFactory.newPredicate(x1);
		IPredicate px2 = predicateFactory.newPredicate(x2);
		IPredicate precondition = px1;
		IPredicate postcondition = px2;
//		postcondition = predicateFactory.not(postcondition);
		precondition = pUnifer.getOrConstructPredicate(precondition);
		postcondition = pUnifer.getOrConstructPredicate(postcondition);

		List<Object> controlLocationSequence = new ArrayList<>();
		controlLocationSequence.add(new Object());
		controlLocationSequence.add(new Object());
		controlLocationSequence.add(new Object());

		InterpolatingTraceCheckCraig<IAction> interpolate = new InterpolatingTraceCheckCraig<>(precondition,
				postcondition, pendingContexts, trace, controlLocationSequence, service, toolkit, managedScript, null,
				pUnifer, AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true,
				InterpolationTechnique.Craig_NestedInterpolation, false, XnfConversionTechnique.BDD_BASED,
				SimplificationTechnique.NONE, false);
		System.out.println(interpolate.isCorrect());
		System.out.println(interpolate.getInterpolants()[0]);
	}

}

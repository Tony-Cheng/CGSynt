package TTC2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
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
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula.Infeasibility;
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
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheck;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;

public class TestTraceCheckConstructor {

	public static void main(String[] args) {
		IUltimateServiceProvider service = UltimateMocks.createUltimateServiceProviderMock();
		ILogger logger = new ConsoleLogger();
		ManagedScript managedScript = new ManagedScript(service, new SMTInterpol(new DefaultLogger()));
		Script script = managedScript.getScript();
		script.setOption(":produce-proofs", true);
		script.setLogic(Logics.QF_LIA);

		DefaultIcfgSymbolTable symbolTable = new DefaultIcfgSymbolTable();
		managedScript.getScript().declareFun("x", new Sort[0], managedScript.getScript().sort("Int"));
		managedScript.getScript().declareFun("x'", new Sort[0], managedScript.getScript().sort("Int"));
		managedScript.getScript().declareFun("y", new Sort[0], managedScript.getScript().sort("Int"));
		managedScript.getScript().declareFun("y'", new Sort[0], managedScript.getScript().sort("Int"));
		BoogieOldVar var1 = new BoogieOldVar("x", (IBoogieType) BoogieType.TYPE_INT,
				managedScript.getScript().variable("x", managedScript.getScript().sort("Int")),
				(ApplicationTerm) managedScript.getScript().term("x"),
				(ApplicationTerm) managedScript.getScript().term("x'"));
		BoogieNonOldVar var2 = new BoogieNonOldVar("x'", (IBoogieType) BoogieType.TYPE_INT,
				managedScript.getScript().variable("x'", managedScript.getScript().sort("Int")),
				(ApplicationTerm) managedScript.getScript().term("x"),
				(ApplicationTerm) managedScript.getScript().term("x'"), var1);
		BoogieOldVar var3 = new BoogieOldVar("y", (IBoogieType) BoogieType.TYPE_INT,
				managedScript.getScript().variable("y", managedScript.getScript().sort("Int")),
				(ApplicationTerm) managedScript.getScript().term("y"),
				(ApplicationTerm) managedScript.getScript().term("y'"));
		BoogieNonOldVar var4 = new BoogieNonOldVar("y'", (IBoogieType) BoogieType.TYPE_INT,
				managedScript.getScript().variable("y'", managedScript.getScript().sort("Int")),
				(ApplicationTerm) managedScript.getScript().term("y"),
				(ApplicationTerm) managedScript.getScript().term("y'"), var1);
		var1.setNonOldVar(var2);
		var3.setNonOldVar(var4);
		symbolTable.add(var2);
		symbolTable.add(var4);

		String procedure1 = "procedure1";
		String procedure2 = "procedure2";
		String procedure3 = "procedure3";
		String procedure4 = "procedure4";
		HashRelation<String, IProgramNonOldVar> mProc2Globals = new HashRelation<>();
		mProc2Globals.addPair(procedure1, var2);
		mProc2Globals.addPair(procedure2, var4);
		mProc2Globals.addPair(procedure3, var2);
		ModifiableGlobalsTable modifiableGlobalsTable = new ModifiableGlobalsTable(mProc2Globals);
		Set<String> procedures = new HashSet<>();
		procedures.add(procedure1);
		procedures.add(procedure2);
		procedures.add(procedure3);
		procedures.add(procedure4);

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

		// TransFormulaBuilder formulaBuilder = new TransFormulaBuilder(null, null,
		// false, null, false, null, false);
		Term one = script.numeral("1");
		// formulaBuilder.addOutVar(var2, var2.getTermVariable());
		// formulaBuilder.setFormula(managedScript.getScript().term("=", var2.getTerm(),
		// one));
		// formulaBuilder.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// UnmodifiableTransFormula formula =
		// formulaBuilder.finishConstruction(managedScript);
		// BasicInternalAction basicAction = new BasicInternalAction(procedure1,
		// procedure2, formula);

		// TransFormulaBuilder formulaBuilder2 = new TransFormulaBuilder(null, null,
		// false, null, false, null, false);
		// formulaBuilder2.addInVar(var1, var1.getTermVariable());
		// formulaBuilder2.addOutVar(var2, var2.getTermVariable());
		// formulaBuilder2.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// UnmodifiableTransFormula formula2 =
		// formulaBuilder2.finishConstruction(managedScript);
		List<IProgramVar> lhs1 = new ArrayList<>();
		List<Term> rhs1 = new ArrayList<>();
		List<IProgramVar> lhs2 = new ArrayList<>();
		List<Term> rhs2 = new ArrayList<>();
		List<IProgramVar> lhs3 = new ArrayList<>();
		List<Term> rhs3 = new ArrayList<>();
		List<IProgramVar> lhs4 = new ArrayList<>();
		List<Term> rhs4 = new ArrayList<>();
		lhs1.add(var1);
		rhs1.add(script.numeral("0"));
		lhs2.add(var3);
		rhs2.add(script.numeral("0"));
		lhs3.add(var1);
		rhs3.add(script.term("+", var1.getTerm(), one));
		lhs4.add(var1);
		rhs4.add(script.numeral("-1"));
		UnmodifiableTransFormula formula1 = TransFormulaBuilder.constructAssignment(lhs1, rhs1, symbolTable,
				managedScript);
		UnmodifiableTransFormula formula2 = TransFormulaBuilder.constructAssignment(lhs2, rhs2, symbolTable,
				managedScript);
		UnmodifiableTransFormula formula3 = TransFormulaBuilder.constructAssignment(lhs3, rhs3, symbolTable,
				managedScript);
		UnmodifiableTransFormula formula4 = TransFormulaBuilder.constructEqualityAssumption(lhs4, rhs4, symbolTable,
				managedScript);
		BasicInternalAction basicAction1 = new BasicInternalAction(procedure1, procedure2, formula1);
		BasicInternalAction basicAction2 = new BasicInternalAction(procedure2, procedure3, formula2);
		BasicInternalAction basicAction3 = new BasicInternalAction(procedure3, procedure4, formula3);
		BasicInternalAction basicAction4 = new BasicInternalAction(procedure4, null, formula4);


		// TransFormulaBuilder formulaBuilder3 = new TransFormulaBuilder(null, null,
		// false, null, false, null, false);
		// formulaBuilder3.addInVar(var1, var1.getTermVariable());
		// formulaBuilder3.setFormula(script.term("=", var1.getTerm(), one));
		// formulaBuilder3.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// UnmodifiableTransFormula formula3 =
		// formulaBuilder3.finishConstruction(managedScript);
		// BasicInternalAction basicAction3 = new BasicInternalAction(procedure3, null,
		// formula3);

		IAction[] word = new IAction[4];
		int[] nestingRelation = new int[4];
		word[0] = basicAction1;
		word[1] = basicAction2;
		word[2] = basicAction3;
		word[3] = basicAction4;
		nestingRelation[0] = NestedWord.INTERNAL_POSITION;
		nestingRelation[1] = NestedWord.INTERNAL_POSITION;
		nestingRelation[2] = NestedWord.INTERNAL_POSITION;
		nestingRelation[3] = NestedWord.INTERNAL_POSITION;

		NestedWord<IAction> trace = new NestedWord<>(word, nestingRelation);
		// NestedWord<IAction> trace = new NestedWord<>(basicAction1,
		// NestedWord.INTERNAL_POSITION);

		BasicPredicateFactory predicateFactory = new BasicPredicateFactory(service, managedScript, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		PredicateUnifier pUnifer = new PredicateUnifier(logger, service, managedScript, predicateFactory, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);

		// TraceCheck<IAction> traceCheck = new TraceCheck<>(pUnifer.getTruePredicate(),
		// pUnifer.getFalsePredicate(),
		// pendingContexts, trace, service, toolkit,
		// AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true);
		// traceCheck.isCorrect();

		List<Object> controlLocationSequence = new ArrayList<>();
		InterpolatingTraceCheckCraig interpolate = new InterpolatingTraceCheckCraig(pUnifer.getTruePredicate(),
				pUnifer.getFalsePredicate(), pendingContexts, trace, controlLocationSequence, service, toolkit,
				managedScript, null, pUnifer, AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true,
				InterpolationTechnique.Craig_NestedInterpolation, false, XnfConversionTechnique.BDD_BASED,
				SimplificationTechnique.NONE, false);
		IPredicate[] preds = interpolate.getInterpolants();

	}

}

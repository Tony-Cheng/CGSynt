package test.generalization;

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

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ConcurrencyInformation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ModifiableGlobalsTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.SmtSymbols;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ThreadInstance;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgForkTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgJoinTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ILocalProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;

public class TestTraceGeneralization {	
	@Test
	void test1() throws Exception{
		IUltimateServiceProvider service = TraceGlobalVariables.getGlobalVariables().getService();
		ILogger logger = new ConsoleLogger();
		ManagedScript managedScript = TraceGlobalVariables.getGlobalVariables().getManagedScript();
		Script script = managedScript.getScript();
		script.setOption(":produce-proofs", true);
		script.setLogic(Logics.QF_LIA);
		
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		BoogieNonOldVar y = vf.constructVariable("y", VariableFactory.INT);
		
		DefaultIcfgSymbolTable symbolTable = new DefaultIcfgSymbolTable();
		symbolTable.add(x);
		symbolTable.add(y);
		
		HashRelation<String, IProgramNonOldVar> proc2Globals = new HashRelation<>();
		proc2Globals.addPair("p1", x);
		proc2Globals.addPair("p3", x);
		
		ModifiableGlobalsTable modifiableGlobalsTable = new ModifiableGlobalsTable(proc2Globals);
		Set<String> procedures = new HashSet<>();
		procedures.add("p0");
		procedures.add("p1");
		procedures.add("p2");
		procedures.add("p3");
		procedures.add("p4");
		procedures.add("p5");
		
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
		
		List<IProgramVar> 	lhs0 = new ArrayList<>();
		List<Term> 			rhs0 = new ArrayList<>();
		List<IProgramVar> 	lhs1 = new ArrayList<>();
		List<Term> 			rhs1 = new ArrayList<>();
		List<IProgramVar> 	lhs2 = new ArrayList<>();
		List<Term> 			rhs2 = new ArrayList<>();
		List<IProgramVar> 	lhs3 = new ArrayList<>();
		List<Term> 			rhs3 = new ArrayList<>();
		List<IProgramVar> 	lhs4 = new ArrayList<>();
		List<Term> 			rhs4 = new ArrayList<>();
		List<IProgramVar> 	lhs5 = new ArrayList<>();
		List<Term> 			rhs5 = new ArrayList<>();
		
		lhs0.add(x);
		rhs0.add(y.getTerm());
		
		lhs1.add(x);
		rhs1.add(script.numeral("0"));
		
		lhs2.add(x);
		rhs2.add(y.getTerm());
		
		lhs3.add(x);
		rhs3.add(script.term("+", x.getTerm(), script.numeral("1")));
		
		lhs4.add(x);
		rhs4.add(y.getTerm());
		
		lhs5.add(x);
		rhs5.add(y.getTerm());
		
		ExtendedTransFormulaBuilder formulaBuilder = new ExtendedTransformulaBuilder();
	}
}

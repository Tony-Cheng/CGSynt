package test.generalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.nfa.TraceGeneralization;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestTraceGeneralization {	
	@Test
	void test1() throws Exception{
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();	
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		BoogieNonOldVar y = vf.constructVariable("y", VariableFactory.INT);
		
		IStatement s0 = new ScriptAssumptionStatement(x, y.getTerm(), "<=");
		IStatement s1 = new ScriptAssignmentStatement(x, script.numeral("0"));
		IStatement xley = new ScriptAssumptionStatement(x, y.getTerm(), "<");
		IStatement s3 = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
		IStatement s4 = new ScriptAssumptionStatement(x, y.getTerm(), ">=");
		
		List<IStatement> statements = new ArrayList<>();
		statements.add(s0);
		statements.add(s1);
		statements.add(xley);
		statements.add(s3);
		statements.add(s4);
		statements.add(xley);
		
		IPredicate[] predicates = TraceToInterpolants.getTraceToInterpolants().computeInterpolants(statements);
		
		Set<IPredicate> interpolants = new HashSet<>(Arrays.asList(predicates));
		Set<IStatement> tokens = new HashSet<>(statements);
		
		System.exit(0);
		
		TraceGeneralization generalize = new TraceGeneralization(interpolants, tokens);
		NestedWordAutomaton<IStatement, IPredicate> generalTrace = generalize.getResult();
		
		for (IPredicate pred : predicates) {
			System.out.println(pred);
		}
	}
}

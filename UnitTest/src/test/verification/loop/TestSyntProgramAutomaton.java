package test.verification.loop;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.Verification.MainVerificationLoop;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.tree.buchi.operations.ProgramAutomatonConstruction;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;

public class TestSyntProgramAutomaton {
	@Test
	public void test1() throws Exception {
		// Assignment: i=0 i=1 i=2
		// Assumption: i>=0
		MainVerificationLoop.resetAll();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		IStatement ie0 = new ScriptAssignmentStatement(i, script.numeral("0"));
		IStatement ie1 = new ScriptAssignmentStatement(i, script.numeral("1"));
		IStatement ie2 = new ScriptAssignmentStatement(i, script.numeral("2"));
		IStatement ige0 = new ScriptAssumptionStatement(i, script.numeral("0"), ">=");
		Set<IStatement> statements = new HashSet<IStatement>();
		statements.add(ie0);
		statements.add(ige0);
		statements.add(ie1);
		statements.add(ie2);
		ProgramAutomatonConstruction construct = new ProgramAutomatonConstruction(statements);
		construct.computeResult();
		System.out.println("Test 1");
		System.out.println(construct.getResult());
		System.out.println("Transition Alphabet:");
		for (int j = 0; j < construct.getAlphabet().size(); j++) {
			System.out.println(construct.getAlphabet().get(j));
		}
		System.out.println();
	}
}

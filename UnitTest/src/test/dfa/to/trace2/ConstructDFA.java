package test.dfa.to.trace2;

import java.util.HashSet;
import java.util.Set;

import cgsynt.trace2.Assignment;
import cgsynt.trace2.Assumption;
import cgsynt.trace2.Formula;
import cgsynt.trace2.Numerical;
import cgsynt.trace2.StandardFormula;
import cgsynt.trace2.Statement;
import cgsynt.trace2.Variable;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.logic.Sort;

public class ConstructDFA {

	public static NestedWordAutomaton<Statement, String> dfa1(AutomataLibraryServices service, Variable x, Variable y) {
		// (x:=0)(y:=0)(x++)*(x==-1)

		Formula form1 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form2 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form3 = new StandardFormula("+", x, new Numerical("1"));
		Formula form4 = new StandardFormula("=", x, new Numerical("-1"));

		Statement s1 = new Assignment(x, form1);
		Statement s2 = new Assignment(y, form2);
		Statement s3 = new Assignment(x, form3);
		Statement s4 = new Assumption(form4);

		Set<Statement> letters = new HashSet<>();
		letters.add(s1);
		letters.add(s2);
		letters.add(s3);
		letters.add(s4);

		VpAlphabet<Statement> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Statement, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, false, "q1");
		nwa.addState(false, false, "q2");
		nwa.addState(false, false, "q3");
		nwa.addState(false, true, "q4");

		nwa.addInternalTransition("q0", s1, "q1");
		nwa.addInternalTransition("q1", s2, "q2");
		nwa.addInternalTransition("q2", s3, "q3");
		nwa.addInternalTransition("q3", s3, "q3");
		nwa.addInternalTransition("q3", s4, "q4");

		return nwa;

	}
	
	public static NestedWordAutomaton<Statement, String> dfa2(AutomataLibraryServices service, Variable x) {
		// (x:=0)(y:=0)(x++)*(x==-1)

		Formula form1 = new StandardFormula("+", new Numerical("3"), new Numerical("0"));
		Formula form2 = new StandardFormula("*", x, new Numerical("3"));
		Formula form3 = new StandardFormula(">", x, new Numerical("1000"));
		Formula form4 = new StandardFormula("<", x, new Numerical("1"));

		Statement s1 = new Assignment(x, form1);
		Statement s2 = new Assignment(x, form2);
		Statement s3 = new Assumption(form3);
		Statement s4 = new Assumption(form4);

		Set<Statement> letters = new HashSet<>();
		letters.add(s1);
		letters.add(s2);
		letters.add(s3);
		letters.add(s4);

		VpAlphabet<Statement> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Statement, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, false, "q1");
		nwa.addState(false, false, "q2");
		nwa.addState(false, true, "q3");

		nwa.addInternalTransition("q0", s1, "q1");
		nwa.addInternalTransition("q1", s2, "q2");
		nwa.addInternalTransition("q2", s3, "q1");
		nwa.addInternalTransition("q2", s4, "q3");

		return nwa;

	}
}

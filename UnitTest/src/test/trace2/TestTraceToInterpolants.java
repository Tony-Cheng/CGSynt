package test.trace2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import usra.trace.Token;
import usra.trace.TokenFactory;
import usra.trace2.Assignment;
import usra.trace2.Assume;
import usra.trace2.CraigInterpolant;
import usra.trace2.Formula;
import usra.trace2.Numerical;
import usra.trace2.StandardFormula;
import usra.trace2.Statement;
import usra.trace2.Trace;
import usra.trace2.TraceToCraigInterpolant;
import usra.trace2.Variable;

public class TestTraceToInterpolants {

	@Test
	void test1() {
		// x:=0 y:=0 x++ x==-1
		final Script s = new SMTInterpol(new DefaultLogger());
		s.setOption(":produce-proofs", true);
		s.setLogic(Logics.QF_LIA);
		Variable x = new Variable("x", new Sort[0], "Int");
		Variable y = new Variable("y", new Sort[0], "Int");
		Formula form1 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form2 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form3 = new StandardFormula("+", x, new Numerical("1"));
		Formula form4 = new StandardFormula("=", x, new Numerical("-1"));
		Statement s1 = new Assignment(x, form1);
		Statement s2 = new Assignment(y, form2);
		Statement s3 = new Assignment(x, form3);
		Statement s4 = new Assume(form4);

		Trace trace = new Trace();
		trace.addVariable(x);
		trace.addVariable(y);

		trace.addStatement(s1);
		trace.addStatement(s2);
		trace.addStatement(s3);
		trace.addStatement(s4);

		TraceToCraigInterpolant ttc = new TraceToCraigInterpolant(trace, s);

		CraigInterpolant interpolants = ttc.computeResult();

		for (Term term : interpolants.getTerms()) {
			System.out.println(term);
		}

	}

	@Test
	void test2() {
		// x:=10 x:=x*10 x:=x+10 x>10 x>=20 x==0
		final Script s = new SMTInterpol(new DefaultLogger());
		s.setOption(":produce-proofs", true);
		s.setLogic(Logics.QF_LIA);
		Variable x = new Variable("x", new Sort[0], "Int");
		Formula form1 = new StandardFormula("+", new Numerical("0"), new Numerical("10"));
		Formula form2 = new StandardFormula("*", x, new Numerical("10"));
		Formula form3 = new StandardFormula("+", x, new Numerical("10"));
		Formula form4 = new StandardFormula(">", x, new Numerical("10"));
		Formula form5 = new StandardFormula(">=", x, new Numerical("20"));
		Formula form6 = new StandardFormula("=", x, new Numerical("0"));

		Statement s1 = new Assignment(x, form1);
		Statement s2 = new Assignment(x, form2);
		Statement s3 = new Assignment(x, form3);
		Statement s4 = new Assume(form4);
		Statement s5 = new Assume(form5);
		Statement s6 = new Assume(form6);

		Trace trace = new Trace();
		trace.addVariable(x);

		trace.addStatement(s1);
		trace.addStatement(s2);
		trace.addStatement(s3);
		trace.addStatement(s4);
		trace.addStatement(s5);
		trace.addStatement(s6);

		TraceToCraigInterpolant ttc = new TraceToCraigInterpolant(trace, s);

		CraigInterpolant interpolants = ttc.computeResult();

		for (Term term : interpolants.getTerms()) {
			System.out.println(term);
		}

	}
	
	@Test
	void test3() {
		// x:=0 y:=0 z:=0 x++ y++ z++ x++ x==z
		final Script s = new SMTInterpol(new DefaultLogger());
		s.setOption(":produce-proofs", true);
		s.setLogic(Logics.QF_LIA);
		Variable x = new Variable("x", new Sort[0], "Int");
		Variable y = new Variable("y", new Sort[0], "Int");
		Variable z = new Variable("z", new Sort[0], "Int");

		Formula form1 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form2 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form3 = new StandardFormula("+", new Numerical("0"), new Numerical("0"));
		Formula form4 = new StandardFormula("+", x, new Numerical("1"));
		Formula form5 = new StandardFormula("+", y, new Numerical("1"));
		Formula form6 = new StandardFormula("+", z, new Numerical("1"));
		Formula form7 = new StandardFormula("+", x, new Numerical("1"));
		Formula form8 = new StandardFormula("=", x, z);

		Statement s1 = new Assignment(x, form1);
		Statement s2 = new Assignment(y, form2);
		Statement s3 = new Assignment(z, form3);
		Statement s4 = new Assignment(x, form4);
		Statement s5 = new Assignment(y, form5);
		Statement s6 = new Assignment(z, form6);
		Statement s7 = new Assignment(x, form7);
		Statement s8 = new Assume(form8);

		Trace trace = new Trace();
		trace.addVariable(x);
		trace.addVariable(y);
		trace.addVariable(z);

		trace.addStatement(s1);
		trace.addStatement(s2);
		trace.addStatement(s3);
		trace.addStatement(s4);
		trace.addStatement(s5);
		trace.addStatement(s6);
		trace.addStatement(s7);
		trace.addStatement(s8);


		TraceToCraigInterpolant ttc = new TraceToCraigInterpolant(trace, s);

		CraigInterpolant interpolants = ttc.computeResult();

		for (Term term : interpolants.getTerms()) {
			System.out.println(term);
		}

	}
}

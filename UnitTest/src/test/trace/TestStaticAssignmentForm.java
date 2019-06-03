package test.trace;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.FormulaFactory;
import cgsynt.Token;
import cgsynt.TokenFactory;
import cgsynt.Trace;
import cgsynt.trace.operations.TraceToSMTFormula;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class TestStaticAssignmentForm {

	@BeforeAll
	static void init() {
	}

	@Test
	void convertSimpleTraceToAssignments() {
		// x:=0 y:=0 x++ x==-1
		Trace trace = new Trace();
		TokenFactory factory = new TokenFactory();
		Token x = factory.createVariable();
		Token y = factory.createVariable();
		trace.addFormulas(FormulaFactory.assign(x, 0), FormulaFactory.assign(y, 0),
				FormulaFactory.assign(x, FormulaFactory.addition(x, 1)), FormulaFactory.assume(x, -1));
		TraceToSMTFormula traceToFormula = new TraceToSMTFormula(trace);
		Script result = traceToFormula.getResult();
		Term[] terms = result.getAssertions();
		assertEquals(terms.length, 4);
		System.out.println("Trace: x:=0 y:=0 x++ x==-1");
		for (Term term : terms) {
			System.out.println(term);
		}
		System.out.println();
	}

	@Test
	void convertComplexSingleVariableTrace() {
		// x:=10 x:=x*10 x:=x+10 x>10 x>=20 x==0
		Trace trace = new Trace();
		TokenFactory factory = new TokenFactory();
		Token x = factory.createVariable();
		trace.addFormulas(FormulaFactory.assign(x, 10), FormulaFactory.assign(x, FormulaFactory.mult(x, 10)),
				FormulaFactory.assign(x, FormulaFactory.addition(x, 10)), FormulaFactory.gt(x, 10),
				FormulaFactory.geq(x, 20), FormulaFactory.assume(x, 0));
		TraceToSMTFormula traceToFormula = new TraceToSMTFormula(trace);
		Script result = traceToFormula.getResult();
		Term[] terms = result.getAssertions();
		assertEquals(terms.length, 6);
		System.out.println("Trace: x:=10 x:=x*10 x:=x+10 x>10 x>=20 x==0");
		for (Term term : terms) {
			System.out.println(term);
		}
		System.out.println();
	}

	@Test
	void convertComplexMultiVariable() {
		// x:=0 y:=0 z:=0 x++ y++ z++ x++ x==z
		Trace trace = new Trace();
		TokenFactory factory = new TokenFactory();
		Token x = factory.createVariable();
		Token y = factory.createVariable();
		Token z = factory.createVariable();
		trace.addFormulas(FormulaFactory.assign(x, 0), FormulaFactory.assign(y, 0), FormulaFactory.assign(z, 0),
				FormulaFactory.assign(x, FormulaFactory.addition(x, 1)),
				FormulaFactory.assign(y, FormulaFactory.addition(y, 1)),
				FormulaFactory.assign(z, FormulaFactory.addition(z, 1)),
				FormulaFactory.assign(x, FormulaFactory.addition(x, 1)), FormulaFactory.assume(x, z));
		TraceToSMTFormula traceToFormula = new TraceToSMTFormula(trace);
		Script result = traceToFormula.getResult();
		Term[] terms = result.getAssertions();
		assertEquals(terms.length, 8);
		System.out.println("Trace: x:=0 y:=0 z:=0 x++ y++ z++ x++ x==z");
		for (Term term : terms) {
			System.out.println(term);
		}
		System.out.println();
	}
}

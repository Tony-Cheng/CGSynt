package test.trace;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.Formula;
import cgsynt.FormulaInterface;
import cgsynt.Token;
import cgsynt.TokenFactory;
import cgsynt.Trace;

public class TestTrace {

	@BeforeAll
	static void init() {
	}

	@Test
	void testSingleAssignment() {
		TokenFactory factory = new TokenFactory();
		Formula one = new Formula(factory.createNumerical(1));
		Formula assignX = new Formula(factory.createVariable(), "=", one);
		Trace trace = new Trace();
		trace.addFormulas(assignX);
		assertEquals(trace.getNames(), 1);
		List<FormulaInterface> formulas = trace.getFormulas();
		assertEquals(formulas.size(), 1);
		assertEquals(formulas.get(0), assignX);
	}

	@Test
	void testMultipleVariables() {
		TokenFactory factory = new TokenFactory();
		Formula one = new Formula(factory.createVariable());
		Formula assignX = new Formula(factory.createVariable(), "=", one);
		Trace trace = new Trace();
		trace.addFormulas(assignX);
		assertEquals(trace.getNames(), 2);
		List<FormulaInterface> formulas = trace.getFormulas();
		assertEquals(formulas.size(), 1);
		assertEquals(formulas.get(0), assignX);
	}

	@Test
	void testComplexTrace() {
		// x:=0 y:=0 x++ x==-1
		TokenFactory factory = new TokenFactory();
		Token x = factory.createVariable();
		Token y = factory.createVariable();
		Formula first = new Formula(x, "=", new Formula(factory.createNumerical(0)));
		Formula second = new Formula(y, "=", new Formula(factory.createNumerical(0)));
		Formula third = new Formula(x, "=", new Formula(x, "+", new Formula(factory.createNumerical(1))));
		Formula fourth = new Formula(x, "==", new Formula(factory.createNumerical(-1)));
		Trace trace = new Trace();
		trace.addFormulas(first);
		trace.addFormulas(second);
		trace.addFormulas(third);
		trace.addFormulas(fourth);
		assertEquals(trace.getNames(), 2);
		List<FormulaInterface> formulas = trace.getFormulas();
		assertEquals(formulas.size(), 4);
		assertEquals(formulas.get(0).getV1(), x);
		assertEquals(formulas.get(1).getV1(), y);
		assertEquals(formulas.get(2).getV1(), x);
		assertEquals(formulas.get(3).getV1(), x);
		assertEquals(formulas.get(0).getOperation(), "=");
		assertEquals(formulas.get(1).getOperation(), "=");
		assertEquals(formulas.get(2).getOperation(), "=");
		assertEquals(formulas.get(3).getOperation(), "==");
		assertEquals(formulas.get(2).getV2().getOperation(), "+");
		assertEquals(formulas.get(2).getV2().getV1(), x);

	}
}

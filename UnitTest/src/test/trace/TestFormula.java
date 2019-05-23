package test.trace;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import usra.trace.Formula;
import usra.trace.Token;
import usra.trace.TokenFactory;

public class TestFormula {

	@BeforeAll
	static void init() {
	}

	@Test
	void testAssignment() {
		TokenFactory factory = new TokenFactory();
		Formula one = new Formula(factory.createNumerical(1));
		Formula assignX = new Formula(factory.createVariable(), "=", one);
		assertEquals(one.getV1().getType(), Token.NUMERICAL);
		assertEquals(one.getV1().getValue(), 1);
		assertEquals(one.getOperation(), null);
		assertEquals(one.getV2(), null);
		assertEquals(assignX.getV2(), one);
		assertEquals(assignX.getOperation(), "=");
		assertEquals(assignX.getV1().getType(), Token.VARIABLE);
		assertEquals(assignX.getV1().getValue(), 1);
	}

}

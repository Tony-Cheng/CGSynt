package test.trace;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.Token;

class TestToken {

	@BeforeAll
	static void init() {
	}

	@Test
	void testVariable() {
		int i = (int) (Math.random() * 1000);
		int j = (int) (Math.random() * 1000);
		Token t1 = new Token(Token.VARIABLE, i);
		Token t2 = new Token(Token.VARIABLE, j);
		assertEquals(t1.getType(), Token.VARIABLE);
		assertEquals(t1.getValue(), i);
		assertEquals(t2.getType(), Token.VARIABLE);
		assertEquals(t2.getValue(), j);
	}

	@Test
	void testNumerical() {
		int i = (int) (Math.random() * 1000);
		int j = (int) (Math.random() * 1000);
		Token t1 = new Token(Token.NUMERICAL, i);
		Token t2 = new Token(Token.NUMERICAL, j);
		assertEquals(t1.getType(), Token.NUMERICAL);
		assertEquals(t1.getValue(), i);
		assertEquals(t2.getType(), Token.NUMERICAL);
		assertEquals(t2.getValue(), j);
	}
}

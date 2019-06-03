package test.trace;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.Token;
import cgsynt.TokenFactory;

public class TestTokenFactory {

	@BeforeAll
	static void init() {
	}

	@Test
	void testVariable() {
		int n = (int) (Math.random() * 10000);
		Token[] tokens = new Token[n];
		TokenFactory factory = new TokenFactory();
		for (int i = 0; i < n; i++) {
			tokens[i] = factory.createVariable();
		}
		for (int i = 0; i < n; i++) {
			assertEquals(tokens[i].getType(), Token.VARIABLE);
			assertEquals(tokens[i].getValue(), i + 1);
		}
	}

	@Test
	void testNumerical() {
		TokenFactory factory = new TokenFactory();
		for (int i = 0; i < 10000; i++) {
			int j = (int) (Math.random() * 600);
			Token next = factory.createNumerical(j);
			assertEquals(next.getType(), Token.NUMERICAL);
			assertEquals(next.getValue(), j);
		}
	}
}

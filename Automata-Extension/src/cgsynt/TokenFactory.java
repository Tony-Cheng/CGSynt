package cgsynt;

public class TokenFactory {

	/**
	 * Number of variables
	 */
	private int nVars;

	public TokenFactory() {
		nVars = 0;
	}

	/**
	 * Create a new variable.
	 * 
	 * @return
	 */
	public Token createVariable() {
		return new Token(Token.VARIABLE, ++nVars);
	}

	/**
	 * Create a numerical token.
	 * 
	 * @param number
	 *            the number that the token represents.
	 * @return
	 */
	public Token createNumerical(int number) {
		return new Token(Token.NUMERICAL, number);
	}
}

package usra.trace;

public class FormulaFactory {

	final static TokenFactory numericalFactory = new TokenFactory();

	/**
	 * Return the formula x + y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula addition(Token x, Token y) {
		Formula add = new Formula(x, "+", new Formula(y));
		return add;
	}

	/**
	 * Return the formula x + y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula addition(Token x, int y) {
		Formula add = new Formula(x, "+", new Formula(numericalFactory.createNumerical(y)));
		return add;
	}

	/**
	 * Return the formula x - y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula subtract(Token x, Token y) {
		Formula sub = new Formula(x, "-", new Formula(y));
		return sub;

	}

	/**
	 * Return a formula that assigns x to the formula y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula assign(Token x, Formula y) {
		Formula ass = new Formula(x, "=", y);
		return ass;
	}

	/**
	 * Return a formula that assigns x to the integer y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula assign(Token x, int y) {
		Formula ass = new Formula(x, "=", new Formula(numericalFactory.createNumerical(y)));
		return ass;
	}

	/**
	 * Return the formula that represents x==y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula assume(Token x, Token y) {
		Formula ass = new Formula(x, "==", new Formula(y));
		return ass;
	}

	/**
	 * Return the formula that represents x==y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula assume(Token x, int y) {
		Formula ass = new Formula(x, "==", new Formula(numericalFactory.createNumerical(y)));
		return ass;
	}
}

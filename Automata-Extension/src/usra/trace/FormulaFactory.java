package usra.trace;

public class FormulaFactory {

	/**
	 * Return the formula x + y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula Addition(Token x, Token y) {
		Formula add = new Formula(x, "+", new Formula(y));
		return add;
	}

	/**
	 * Return the formula x - y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula Subtract(Token x, Token y) {
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
	public static Formula Assign(Token x, Formula y) {
		Formula ass = new Formula(x, "=", y);
		return ass;

	}

	/**
	 * Return the formula that represents x==y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula Assume(Token x, Token y) {
		Formula ass = new Formula(x, "==", new Formula(y));
		return ass;
	}
}

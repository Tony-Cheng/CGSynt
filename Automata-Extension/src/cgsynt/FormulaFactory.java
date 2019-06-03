package cgsynt;

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
	 * Return the formula x = y.
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
	 * Return the formula x = y.
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
	 * Return the formula that represents x == y.
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
	 * Return the formula that represents x == y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula assume(Token x, int y) {
		Formula ass = new Formula(x, "==", new Formula(numericalFactory.createNumerical(y)));
		return ass;
	}

	/**
	 * Return the formula x > y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula gt(Token x, Token y) {
		Formula gt = new Formula(x, ">", new Formula(y));
		return gt;
	}

	/**
	 * Return x > y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula gt(Token x, int y) {
		Formula gt = new Formula(x, ">", new Formula(numericalFactory.createNumerical(y)));
		return gt;
	}

	/**
	 * Return the formula x >= y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula geq(Token x, Token y) {
		Formula geq = new Formula(x, ">=", new Formula(y));
		return geq;
	}

	/**
	 * Return the formula x >= y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula geq(Token x, int y) {
		Formula geq = new Formula(x, ">=", new Formula(numericalFactory.createNumerical(y)));
		return geq;
	}

	/**
	 * Return the formula x * y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula mult(Token x, Token y) {
		Formula mult = new Formula(x, "*", new Formula(y));
		return mult;
	}

	/**
	 * Return the formula x * y.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Formula mult(Token x, int y) {
		Formula mult = new Formula(x, "*", new Formula(numericalFactory.createNumerical(y)));
		return mult;
	}

}

package usra.trace;

public class Formula implements FormulaInterface {

	/**
	 * The variable on the left hand side of the formula.
	 */
	private int v1;

	/**
	 * The name of the operation.
	 */
	private String operation;

	/**
	 * The variable on the right hand side of the formula.
	 */
	private Formula v2;

	/**
	 * Create a new formula.
	 * 
	 * @param v1
	 *            the variable on the left hand side of the formula.
	 * @param operation
	 *            the name of the operation.
	 * @param v2
	 *            the variable on the right hand side of the formula.
	 */
	public Formula(int v1, String operation, Formula v2) {
		this.v1 = v1;
		this.operation = operation;
		this.v2 = v2;
	}

	/**
	 * Create a formula that represents a single variable.
	 * 
	 * @param v1
	 *            The name of that variable.
	 */
	public Formula(int v1) {
		this.v1 = v1;
		this.operation = null;
		this.v2 = null;
	}

	@Override
	public int getV1() {
		return v1;
	}

	@Override
	public String getOperation() {
		return operation;
	}

	@Override
	public FormulaInterface getV2() {
		return v2;
	}

}
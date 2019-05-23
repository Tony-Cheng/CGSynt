package usra.trace;

public interface FormulaInterface {

	/**
	 * Return the name of the variable on the left hand side of the formula.
	 * 
	 * @return
	 */
	public int getV1();

	/**
	 * Return the name of the operation.
	 * 
	 * @return
	 */
	public String getOperation();

	/**
	 * Return the variable on the right hand side of the formula.
	 * 
	 * @return
	 */
	public FormulaInterface getV2();
}

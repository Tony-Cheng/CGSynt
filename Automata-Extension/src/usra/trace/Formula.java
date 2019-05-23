package usra.trace;

public class Formula implements FormulaInterface {

	private int v1;
	private String operation;
	private Formula v2;

	public Formula(int v1, String operation, Formula v2) {
		this.v1 = v1;
		this.operation = operation;
		this.v2 = v2;
	}

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

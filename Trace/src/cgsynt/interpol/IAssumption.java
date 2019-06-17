package cgsynt.interpol;

public interface IAssumption extends IStatement {
	public void negate();
	public boolean isNegated();
}

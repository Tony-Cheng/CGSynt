package usra.trace;

import java.util.List;

public interface TraceInterface {

	/**
	 * Return the number of variables in the trace. The names of the variables in
	 * the trace are 1, 2, 3,..., and getNames().
	 * 
	 * @return
	 */
	public int getNames();

	/**
	 * Return a list of formulas that represents a trace.
	 * 
	 * @return
	 */
	public List<FormulaInterface> getFormulas();

	/**
	 * Make a copy of this trace.
	 * 
	 * @return
	 */
	public TraceInterface mkcpy();
}

package usra.trace;

public class Token {

	/**
	 * Represents a variable.
	 */
	public static final int VARIABLE = 1;

	/**
	 * Represents a numerical value.
	 */
	public static final int NUMERICAL = 2;

	/**
	 * The type of this token. The type can either be VARIABLE or NUMERICAL.
	 */
	private int type;

	/**
	 * The value of this token. The value is either the name of a variable or the
	 * numerical value that this variable represents.
	 */
	private int value;

	/**
	 * Create a token.
	 * 
	 * @param type
	 *            the type of this token. It can be either NUMERICAL or VARIABLE.
	 * @param value
	 *            the value of this variable. It can be either a numerical value or
	 *            the name of a variable.
	 */
	public Token(int type, int value) {
		this.type = type;
		this.value = value;

	}

	/**
	 * Return the type of this token.
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Return the value of this token.
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}
}

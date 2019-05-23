package usra.trace;

public class Token {

	public static final int VARIABLE = 1;
	public static final int NUMERICAL = 2;

	private int value;
	private int type;

	public Token(int type, int value) {
		this.type = type;
		this.value = value;

	}
	
	public int getType() {
		return type;
	}
	
	public int getValue() {
		return value;
	}
}

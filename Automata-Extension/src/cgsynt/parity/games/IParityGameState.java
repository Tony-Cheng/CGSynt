package cgsynt.parity.games;

/**
 * A state in the parity game.
 *
 */
public interface IParityGameState {

	/**
	 * Return true if this state is an EvaState and false otherwise.
	 * 
	 * @return
	 */
	boolean isEva();

	/**
	 * Return the priority of the state.
	 * 
	 * @return
	 */
	int getRank();
}

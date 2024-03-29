package cgsynt.parity.games;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * The quasi-polynomial time algorithm for parity game emptiness check in the
 * following paper: https://arxiv.org/pdf/1904.12446.pdf
 * 
 * This algorithm is similar to zielonka's algorithm.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class QuasiTimeEmptinessCheckV2<LETTER extends IRankedLetter, STATE extends IParityState> {

	private ParityGame<LETTER, STATE> parityGame;
	private boolean resultComputed;
	private boolean result;
	private Map<IParityGameState, IParityGameState> nonEmptyProof;
	private IParityGameState nonEmptyProofSource;
	private ParityGame<LETTER, STATE> nonEmptyParityGame;

	public QuasiTimeEmptinessCheckV2(ParityGame<LETTER, STATE> parityGame) {
		this.parityGame = parityGame.copy();
		this.resultComputed = false;
		this.nonEmptyProof = new HashMap<>();
	}

	public void computeResult() {
		if (resultComputed)
			return;
		this.resultComputed = true;
		int highestRank = Integer.MIN_VALUE;
		for (IParityGameState state : parityGame.getStates()) {
			if (state.getRank() > highestRank) {
				highestRank = state.getRank();
			}
		}

		if (highestRank == Integer.MIN_VALUE) {
			this.result = true;
			return;
		}
		if (highestRank % 2 == 1) {
			highestRank += 1;
		}
		Set<IParityGameState> winningStates = solveE(parityGame.copy(), highestRank, parityGame.getStates().size(),
				parityGame.getStates().size());

		this.result = true;
		for (IParityGameState state : parityGame.getInitialStates()) {
			if (winningStates.contains(state)) {
				this.result = false;
				this.nonEmptyProofSource = state;
			}
		}

	}

	/**
	 * A set of states that are in the winning region of the even player.
	 * 
	 * @param G
	 * @param h
	 * @param pe
	 * @param po
	 * @return
	 */
	private Set<IParityGameState> solveE(ParityGame<LETTER, STATE> G, int h, int pe, int po) {
		if (G.getStates().isEmpty() || pe <= 1) {
			return new HashSet<>();
		}
		Set<IParityGameState> Nh;
		Set<IParityGameState> Wo;
		ParityGame<LETTER, STATE> H;
		do {
			Nh = computeNh(G, h);
			H = G.copy();
			H.removeStates(ATR(G, Nh, false));
			Wo = solveO(H, h - 1, po / 2, pe);
			G.removeStates(ATR(G, Wo, true));
		} while (!Wo.isEmpty());
		Nh = computeNh(G, h);
		H = G.copy();
		H.removeStates(ATR(G, Nh, false));
		Wo = solveO(H, h - 1, po, pe);
		G.removeStates(ATR(G, Wo, true));
		while (!Wo.isEmpty()) {
			Nh = computeNh(G, h);
			H = G.copy();
			H.removeStates(ATR(G, Nh, false));
			Wo = solveO(H, h - 1, po / 2, pe);
			G.removeStates(ATR(G, Wo, true));
		}
		this.nonEmptyParityGame = G;
		return G.getStates();
	}

	/**
	 * Return a set of states with rank h.
	 * 
	 * @param G
	 * @param h
	 * @return
	 */
	private Set<IParityGameState> computeNh(ParityGame<LETTER, STATE> G, int h) {
		Set<IParityGameState> Nh = new HashSet<>();
		for (IParityGameState state : G.getStates()) {
			if (state.getRank() == h) {
				Nh.add(state);
			}
		}
		return Nh;
	}

	/**
	 * Return a set of states that are in the winning region of the odd player.
	 * 
	 * @param G
	 * @param h
	 * @param po
	 * @param pe
	 * @return
	 */
	private Set<IParityGameState> solveO(ParityGame<LETTER, STATE> G, int h, int po, int pe) {
		if (G.getStates().isEmpty() || po <= 1) {
			return new HashSet<>();
		}
		Set<IParityGameState> Nh;
		Set<IParityGameState> We;
		ParityGame<LETTER, STATE> H;
		do {
			Nh = computeNh(G, h);
			H = G.copy();
			H.removeStates(ATR(G, Nh, true));
			We = solveE(H, h - 1, pe / 2, po);
			G.removeStates(ATR(G, We, false));
		} while (!We.isEmpty());
		Nh = computeNh(G, h);
		H = G.copy();
		H.removeStates(ATR(G, Nh, true));
		We = solveE(H, h - 1, pe, po);
		G.removeStates(ATR(G, We, false));
		while (!We.isEmpty()) {
			Nh = computeNh(G, h);
			H = G.copy();
			H.removeStates(ATR(G, Nh, true));
			We = solveE(H, h - 1, pe / 2, po);
			G.removeStates(ATR(G, We, false));
		}
		return G.getStates();
	}

	/**
	 * Compute all states that can be forced to reach some state in N.
	 * 
	 * @param G
	 * @param N
	 * @param isEva
	 * @return
	 */
	private Set<IParityGameState> ATR(ParityGame<LETTER, STATE> G, Set<IParityGameState> N, boolean isEva) {
		Set<IParityGameState> atr = new HashSet<>();
		Stack<IParityGameState> toVisit = new Stack<>();
		for (IParityGameState state : N) {
			if ((isEva && state.isEva()) || (!isEva && !state.isEva())) {
				atr.add(state);
				toVisit.add(state);
			}
		}
		while (!toVisit.isEmpty()) {
			IParityGameState next = toVisit.pop();
			for (IParityGameState v : G.getInverseTransitions().get(next)) {
				if ((isEva && v.isEva()) || (!isEva && !v.isEva())) {
					if (!atr.contains(v)) {
						toVisit.push(v);
						atr.add(v);
						if (!isEva) {
							nonEmptyProof.put(v, next);
						}
					}
				} else {
					boolean isInATR = true;
					for (IParityGameState dest : G.getTransitions().get(v)) {
						if (!atr.contains(dest)) {
							isInATR = false;
						}
					}
					if (isInATR && !atr.contains(v)) {
						atr.add(v);
						toVisit.push(v);
					}
				}
			}
		}
		return atr;
	}

	public boolean getResult() {
		return result;
	}

	public Map<IParityGameState, IParityGameState> getNonEmptyProof() {
		return nonEmptyProof;
	}

	public IParityGameState getNonEmptyProofSource() {
		return nonEmptyProofSource;
	}

	public ParityGame<LETTER, STATE> getNonEmptyParityGame() {
		return nonEmptyParityGame;
	}
}

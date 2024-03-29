package cgsynt.parity.games;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * This class is now deprecated.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class QuasiTimeEmptinessCheck<LETTER extends IRankedLetter, STATE extends IParityState> {

	private ParityGame<LETTER, STATE> parityGame;
	private boolean resultComputed;
	private boolean result;

	public QuasiTimeEmptinessCheck(ParityGame<LETTER, STATE> parityGame) {
		this.parityGame = parityGame.copy();
		this.resultComputed = false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		this.resultComputed = true;
		int highestRank = Integer.MIN_VALUE;
		for (IParityGameState state : parityGame.getStates()) {
			if (state.getRank() % 2 == 0 && state.getRank() > highestRank) {
				highestRank = state.getRank();
			} else if (state.getRank() % 2 == 1 && state.getRank() > highestRank) {
				highestRank = state.getRank();
			}
		}

		if (highestRank == Integer.MIN_VALUE) {
			this.result = true;
			return;
		}
		int highestEvenRank = highestRank;
		int highestOddRank = highestRank;
		if (highestRank % 2 == 0) {
			highestOddRank += 1;
		} else {
			highestEvenRank += 1;
		}
		Set<IParityGameState> winningStates = solveE(parityGame.copy(), highestEvenRank, parityGame.getStates().size(),
				parityGame.getStates().size());
		Set<IParityGameState> notWinningStates = solveO(parityGame.copy(), highestOddRank,
				parityGame.getStates().size(), parityGame.getStates().size());
		this.result = true;
		for (IParityGameState state : parityGame.getInitialStates()) {
			if (state.getRank() % 2 == 0 && winningStates.contains(state)) {
				this.result = false;
			}
			if (state.getRank() % 2 == 1 && !notWinningStates.contains(state)) {
				this.result = false;
			}
		}

	}

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
			H.removeStates(ATR(G, Nh));
			Wo = solveO(H, h - 1, po / 2, pe);
			G.removeStates(ATR(G, Wo));
		} while (!Wo.isEmpty());
		Nh = computeNh(G, h);
		H = G.copy();
		H.removeStates(ATR(G, Nh));
		Wo = solveO(H, h - 1, po, pe);
		G.removeStates(ATR(G, Wo));
		while (!Wo.isEmpty()) {
			Nh = computeNh(G, h);
			H = G.copy();
			H.removeStates(ATR(G, Nh));
			Wo = solveO(H, h - 1, po / 2, pe);
			G.removeStates(ATR(G, Wo));
		}
		return G.getStates();
	}

	private Set<IParityGameState> computeNh(ParityGame<LETTER, STATE> G, int h) {
		Set<IParityGameState> Nh = new HashSet<>();
		for (IParityGameState state : G.getStates()) {
			if (state.getRank() == h) {
				Nh.add(state);
			}
		}
		return Nh;
	}

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
			H.removeStates(ATR(G, Nh));
			We = solveE(H, h - 1, pe / 2, po);
			G.removeStates(ATR(G, We));
		} while (!We.isEmpty());
		Nh = computeNh(G, h);
		H = G.copy();
		H.removeStates(ATR(G, Nh));
		We = solveE(H, h - 1, pe, po);
		G.removeStates(ATR(G, We));
		while (!We.isEmpty()) {
			Nh = computeNh(G, h);
			H = G.copy();
			H.removeStates(ATR(G, Nh));
			We = solveE(H, h - 1, pe / 2, po);
			G.removeStates(ATR(G, We));
		}
		return G.getStates();
	}

	private Set<IParityGameState> ATR(ParityGame<LETTER, STATE> G, Set<IParityGameState> N) {
		Set<IParityGameState> atr = new HashSet<>();
		atr.addAll(N);
		Set<IParityGameState> notOpponent = new HashSet<>();
		Set<IParityGameState> opponent = new HashSet<>();
		Stack<IParityGameState> toVisit = new Stack<>();
		toVisit.addAll(N);
		notOpponent.addAll(N);
		while (!toVisit.isEmpty()) {
			IParityGameState next = toVisit.pop();
			if (opponent.contains(next)) {
				for (IParityGameState v : G.getInverseTransitions().get(next)) {
					if (!notOpponent.contains(v)) {
						boolean nonEmptyIntersect = false;
						for (IParityGameState state : G.getTransitions().get(v)) {
							if (opponent.contains(state)) {
								nonEmptyIntersect = true;
								break;
							}
						}
						if (nonEmptyIntersect) {
							notOpponent.add(v);
							atr.add(v);
							toVisit.push(v);
						}
					}
				}
			} else if (notOpponent.contains(next)) {
				for (IParityGameState v : G.getInverseTransitions().get(next)) {
					if (!opponent.contains(v) && notOpponent.containsAll(G.getTransitions().get(v))) {
						opponent.add(v);
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
}

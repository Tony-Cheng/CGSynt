package cgsynt.tree.parity;

public interface IParityState {
	int getRank();
	IParityState makeCpy();
	IParityState getSimpleRepresentation();
}

package interpreter;

/*
 * hält alle relevanten Informationen nach Übergang
 */
public class Transition {

	private State newState;
	private String symbolOut;
	private String direction;

	public Transition(State state, String symbolOut, String direction) {
		this.newState = state;
		this.symbolOut = symbolOut;
		this.direction = direction;
	}

	public State getNewState() {
		return newState;
	}

	public void setNewState(State newState) {
		this.newState = newState;
	}

	public String getSymbolOut() {
		return symbolOut;
	}

	public void setSymbolOut(String symbolOut) {
		this.symbolOut = symbolOut;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String toString() {

		StringBuilder s = new StringBuilder().append(newState.getName()).append(" ").append(symbolOut).append(" ")
				.append(direction);
		return s.toString();
	}

}

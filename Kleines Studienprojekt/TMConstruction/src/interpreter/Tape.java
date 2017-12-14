package interpreter;

import java.util.Stack;
import java.util.regex.Pattern;

/*
 * Simuliert das Band einer TM mit einem aktuellen Symbol und den Bewegungen L und R.
 */

public class Tape {

	private static final String COMP_SYMBOL_Q = "q";
	private static final String COMP_SYMBOL_S = "s";
	private static final String REGEX_Q = "(.+;.+;[+];.+)";
	private static final String REGEX_S = "(.+;.+;[-];.+)";

	private Stack<String> leftTape;
	private Stack<String> rightTape;
	private String currentSymbol;
	private String qSymbol;
	private String sSymbol;

	public Tape() {
		leftTape = new Stack<>();
		rightTape = new Stack<>();
		currentSymbol = "";
	}

	public void moveLeft() {
		rightTape.push(currentSymbol);
		if (leftTape.isEmpty()) {
			currentSymbol = "#";
		} else {
			currentSymbol = leftTape.pop();
		}
	}

	public void moveRight() {
		leftTape.push(currentSymbol);
		if (rightTape.isEmpty()) {
			currentSymbol = "#";
		} else {
			currentSymbol = rightTape.pop();
		}
	}

	// konstruiert ein Tape aus einem String
	public void readTapeFromString(String tape) {
		String[] tapeArray = tape.split(" ");
		int index = 0;

		try {
			while (!tapeArray[index].equals("[")) {
				leftTape.push(tapeArray[index]);
				index++;
			}

			currentSymbol = tapeArray[index + 1];
			index = tapeArray.length - 1;

			while (!tapeArray[index].equals("]")) {
				rightTape.push(tapeArray[index]);
				index--;
			}

		} catch (IndexOutOfBoundsException e) {
			System.err.println("malformed Tape-Description!");
			e.printStackTrace();
		}
	}

	public String toString() {
		StringBuilder tape = new StringBuilder();
		tape.append("# # ");

		for (int i = 0; i < leftTape.size(); i++) {
			tape.append(leftTape.elementAt(i) + " ");
		}

		tape.append("[ ");
		tape.append(currentSymbol + " ");
		tape.append("] ");

		for (int i = rightTape.size() - 1; i >= 0; i--) {
			tape.append(rightTape.elementAt(i) + " ");
		}

		tape.append("# # ");

		return tape.toString();
	}

	public String toString2States(boolean texOutput) {
		StringBuilder tape = new StringBuilder();
		tape.append("# # ");

		for (int i = 0; i < leftTape.size(); i++) {
			if (Pattern.matches(REGEX_Q, leftTape.elementAt(i))) {
				tape.append(COMP_SYMBOL_Q + " ");
				qSymbol = leftTape.elementAt(i);
			} else if (Pattern.matches(REGEX_S, leftTape.elementAt(i))) {
				tape.append(COMP_SYMBOL_S + " ");
				sSymbol = leftTape.elementAt(i);
			} else {
				tape.append(leftTape.elementAt(i) + " ");
			}
		}

		tape.append("[ ");

		if (Pattern.matches(REGEX_Q, currentSymbol)) {
			tape.append(COMP_SYMBOL_Q + " ");
			qSymbol = currentSymbol;
		} else if (Pattern.matches(REGEX_S, currentSymbol)) {
			tape.append(COMP_SYMBOL_S + " ");
			sSymbol = currentSymbol;
		} else {
			tape.append(currentSymbol + " ");
		}

		tape.append("] ");

		for (int i = rightTape.size() - 1; i >= 0; i--) {
			if (Pattern.matches(REGEX_Q, rightTape.elementAt(i))) {
				tape.append(COMP_SYMBOL_Q + " ");
				qSymbol = rightTape.elementAt(i);
			} else if (Pattern.matches(REGEX_S, rightTape.elementAt(i))) {
				tape.append(COMP_SYMBOL_S + " ");
				sSymbol = rightTape.elementAt(i);
			} else {
				tape.append(rightTape.elementAt(i) + " ");
			}
		}

		tape.append("# # ");
		if (!texOutput) {
			tape.append("\nComplexSymbol " + COMP_SYMBOL_Q + " = " + qSymbol);
			tape.append("\nComplexSymbol " + COMP_SYMBOL_S + " = " + sSymbol);
		} else if (!(qSymbol == null || sSymbol == null)){
			String[] qSymbolArray = qSymbol.split(";");
			String[] sSymbolArray = sSymbol.split(";");

			tape.append("\nComplexSymbol " + COMP_SYMBOL_Q + " = " + qSymbolArray[0] + "_{" + qSymbolArray[1] + ","
					+ qSymbolArray[2] + "," + qSymbolArray[3] + "}");
			tape.append("\nComplexSymbol " + COMP_SYMBOL_S + " = " + sSymbolArray[0] + "_{" + sSymbolArray[1] + ","
					+ sSymbolArray[2] + "," + sSymbolArray[3] + "}");

		}
		return tape.toString();
	}


	public String getCurrentSymbol() {
		return currentSymbol;
	}

	public void setCurrentSymbol(String currentSymbol) {
		this.currentSymbol = currentSymbol;
	}

}

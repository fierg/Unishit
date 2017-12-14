package interpreter;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;
import java.util.regex.Pattern;

/*
 * Simuliert das Band einer TM mit einem aktuellen Symbol und den Bewegungen L und R.
 */

public class Tape {

	public static final Set<String> LATEX_ESCAPE_SYMBOLS = new HashSet<String>(Arrays.asList(new String[] { "#" }));
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
		if (texOutput) {
			tape.append("$");
			tape.append("\\# \\#");
		} else {
			tape.append("# # ");
		}

		for (int i = 0; i < leftTape.size(); i++) {
			if (Pattern.matches(REGEX_Q, leftTape.elementAt(i))) {
				tape.append(COMP_SYMBOL_Q + " ");
				qSymbol = leftTape.elementAt(i);
			} else if (Pattern.matches(REGEX_S, leftTape.elementAt(i))) {
				tape.append(COMP_SYMBOL_S + " ");
				sSymbol = leftTape.elementAt(i);
			} else if (LATEX_ESCAPE_SYMBOLS.contains(leftTape.elementAt(i))) {
				tape.append("\\" + leftTape.elementAt(i) + " ");
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
		}else if(Tape.LATEX_ESCAPE_SYMBOLS.contains(currentSymbol)) {
			tape.append("\\" +currentSymbol + " ");
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
			} else if (LATEX_ESCAPE_SYMBOLS.contains(rightTape.elementAt(i))) {
				tape.append("\\" + rightTape.elementAt(i) + " ");
			} else {
				tape.append(rightTape.elementAt(i) + " ");
			}
		}

		if (!texOutput) {
			tape.append("# # ");
			tape.append("\nComplexSymbol " + COMP_SYMBOL_Q + " = " + qSymbol);
			tape.append("\nComplexSymbol " + COMP_SYMBOL_S + " = " + sSymbol);
		} else {
			tape.append("\\# \\# $ \\\\");
			tape.append("\nComplexSymbol " + COMP_SYMBOL_Q + " = $" + State.getNameAsTex(qSymbol) + "$ \\\\");
			tape.append("\nComplexSymbol " + COMP_SYMBOL_S + " = $" + State.getNameAsTex(sSymbol) + "$ \\\\ \n \\medskip");
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

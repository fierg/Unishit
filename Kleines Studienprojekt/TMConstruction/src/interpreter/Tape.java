package interpreter;
import java.io.File;
import java.util.Stack;

public class Tape {

	private Stack<String> leftTape;
	private Stack<String> rightTape;
	private String currentSymbol;

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

			 while(!tapeArray[index].equals("]")){
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

		for (int i = rightTape.size() -1; i >= 0; i--) {
			tape.append(rightTape.elementAt(i) + " ");
		}

		tape.append("# # ");

		return tape.toString();
	}

	public String getCurrentSymbol() {
		return currentSymbol;
	}

	public void setCurrentSymbol(String currentSymbol) {
		this.currentSymbol = currentSymbol;
	}

}

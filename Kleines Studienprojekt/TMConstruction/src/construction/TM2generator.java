package construction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;

import interpreter.Tape;

public class TM2generator {

	public static final int OLD_STATE = 0;
	public static final int NEW_STATE = 1;
	public static final int OLD_SYMBOL = 2;
	public static final int NEW_SYMBOL = 3;
	public static final int DIRECTION = 4;
	public static final String STATES = "states";
	public static final String TRANSITIONS = "transitions";
	public static final String SYMBOLS = "symbols";
	public static final String TAPE = "tape";
	public static final String ALPHA = "q0";
	public static final String BETA = "q1";

	private String[][] compSymbolTable;
	private String[] sigma;
	private String[] states;
	private String[] transitions;
	private LinkedList<String> transitionsNew;
	private String tape;

	public static void main(String[] args) {
		if (args.length >= 1) {
			String filename = args[0];
			TM2generator gen = new TM2generator(args[0]);
			gen.generate2StateTM();
			if (args.length == 2 && "-debug".equals(args[1])) {
				System.out.println("Transitions:\n\n");
				gen.printTransitions();
			}
			gen.writeTM2toFile(filename.split(".tur")[0] + "_2S" + ".tur");
		} else {
			System.out.println("Usage: <path> (.tur file needed!) <-debug>");
		}
	}

	public TM2generator(String[] sigmaA, String[] oldStates, String[] oldTransitions) {
		transitionsNew = new LinkedList<>();
		sigma = Arrays.copyOf(sigmaA, sigmaA.length);
		states = Arrays.copyOf(oldStates, oldStates.length);
		transitions = Arrays.copyOf(oldTransitions, oldTransitions.length);
	}

	public TM2generator(String path) {
		readTMfromFile(path);

	}

	public void generate2StateTM() {
		if (sigma == null || states == null || transitions == null || tape == null || tape == "") {
			throw new IllegalArgumentException("sigma, states, tape or transitions equals null!");
		}
		generateComSymbolTable();
		generateCompTransitions();
		generateNativeTransitions();
		modifyInitialSymbol();

	}

	public void printTransitions() {
		for (String string : transitionsNew) {
			System.out.println(string);
		}
	}

	public void writeTM2toFile(String filename) {
		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
			out.print(get2StateTM());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String get2StateTM() {
		StringBuilder sb = new StringBuilder();

		sb.append(STATES + "\n" + ALPHA + "\n" + BETA + "\n\n");

		sb.append(TRANSITIONS + "\n");
		for (String string : transitionsNew) {
			sb.append(string);
			sb.append("\n");
		}
		sb.append("\n" + SYMBOLS + "\n");
		for (String symbol : sigma) {
			sb.append(symbol);
			sb.append(" ");
		}
		for (String[] symbols : compSymbolTable) {
			for (String symbol : symbols) {
				sb.append(symbol);
				sb.append(" ");
			}
		}

		sb.append("\n\n" + TAPE + "\n");
		sb.append(this.tape);

		return sb.toString();
	}

	private void readTMfromFile(String path) {
		LinkedList<String> states = new LinkedList<>();
		LinkedList<String> transitions = new LinkedList<>();
		String[] symbols = null;

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			for (String line; (line = br.readLine()) != null;) {
				if (STATES.equals(line)) {
					while ((line = br.readLine()) != null && !"transitions".equals(line) && !"".equals(line)) {
						states.add(line);
					}
				} else if (TRANSITIONS.equals(line)) {
					while ((line = br.readLine()) != null && !"symbols".equals(line) && !"".equals(line)) {
						transitions.add(line);
					}
				} else if (SYMBOLS.equals(line)) {
					line = br.readLine();
					symbols = line.split(" ");
				} else if (TAPE.equals(line)) {
					this.tape = br.readLine();
				}
			}

			this.transitionsNew = new LinkedList<>();
			this.states = states.toArray(new String[states.size()]);
			this.sigma = symbols;
			this.transitions = transitions.toArray(new String[transitions.size()]);

		} catch (IOException e) {
			System.err.println("Failed to read TM from File!");
			e.getMessage();
		}
	}

	private void generateNativeTransitions() {

		for (String trans : transitions) {
			String result = ALPHA + "\t";
			String[] transArray = trans.split(" ");

			int oldSymbolIndex = indexOf(sigma, transArray[OLD_SYMBOL]);
			int oldStateIndex = indexOf(states, transArray[OLD_STATE]);
			int newSymbolIndex = indexOf(sigma, transArray[NEW_SYMBOL]);
			int newStateIndex = indexOf(states, transArray[NEW_STATE]);

			if ("R".equals(transArray[DIRECTION])) {
				try {
					result = result + BETA + "\t";
					result = result + compSymbolTable[oldSymbolIndex][oldStateIndex * 4] + "\t";
					result = result + compSymbolTable[newSymbolIndex][newStateIndex * 4 + 2] + "\t";
					result = result + "R\t";

					transitionsNew.add(result);

					result = ALPHA + "\t" + BETA + "\t";
					result = result + compSymbolTable[oldSymbolIndex][oldStateIndex * 4 + 1] + "\t";
					result = result + compSymbolTable[newSymbolIndex][newStateIndex * 4 + 2] + "\t";
					result = result + "R\t";

				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException("invalid transition!");
				}
			} else if ("L".equals(transArray[DIRECTION])) {
				try {
					result = ALPHA + "\t" + ALPHA + "\t";
					result = result + compSymbolTable[oldSymbolIndex][oldStateIndex * 4] + "\t";
					result = result + compSymbolTable[newSymbolIndex][newStateIndex * 4 + 3] + "\t";
					result = result + "L\t";

					transitionsNew.add(result);

					result = ALPHA + "\t" + ALPHA + "\t";
					result = result + compSymbolTable[oldSymbolIndex][oldStateIndex * 4 + 1] + "\t";
					result = result + compSymbolTable[newSymbolIndex][newStateIndex * 4 + 3] + "\t";
					result = result + "L\t";
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException("invalid transition! " + e.getCause());
				}
			} else {
				throw new IllegalArgumentException("invalid transition! direction of transition was not L or R!");
			}

			transitionsNew.add(result);
		}
	}

	private String[] generateSymbolArray(String symbol, String[] states) {
		LinkedList<String> result = new LinkedList<>();

		for (String state : states) {
			result.add(new ComplexSymbol(symbol, state, ComplexSymbol.INFORMATION_MINUS, ComplexSymbol.DIRECTION_R)
					.toString());
			result.add(new ComplexSymbol(symbol, state, ComplexSymbol.INFORMATION_MINUS, ComplexSymbol.DIRECTION_L)
					.toString());
			result.add(new ComplexSymbol(symbol, state, ComplexSymbol.INFORMATION_PLUS, ComplexSymbol.DIRECTION_R)
					.toString());
			result.add(new ComplexSymbol(symbol, state, ComplexSymbol.INFORMATION_PLUS, ComplexSymbol.DIRECTION_L)
					.toString());
		}

		return result.toArray(new String[4 * states.length]);

	}

	private String[][] generateComSymbolTable() {
		compSymbolTable = new String[sigma.length][];

		for (int index = 0; index < sigma.length; index++) {
			compSymbolTable[index] = generateSymbolArray(sigma[index], states);
		}

		return compSymbolTable;
	}

	private void generateCompTransitions() {

		// generiere Übergänge nach Gleichung (1)
		for (int index = 0; index < sigma.length; index++) {
			transitionsNew.add(ALPHA + "\t" + ALPHA + "\t" + sigma[index] + "\t" + compSymbolTable[index][0] + "\tR");
		}

		// generiere Übergänge nach Gleichung (2)
		for (int index = 0; index < sigma.length; index++) {
			transitionsNew.add(BETA + "\t" + ALPHA + "\t" + sigma[index] + "\t" + compSymbolTable[index][1] + "\tL");
		}

		// generiere Übergänge nach Gleichung (3)
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 0; index < compSymbolTable[symbolclass].length - 4; index = index + 4) {
				transitionsNew.add(BETA + "\t" + ALPHA + "\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index + 4] + "\tR");
			}
		}
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 1; index < compSymbolTable[symbolclass].length - 4; index = index + 4) {
				transitionsNew.add(BETA + "\t" + ALPHA + "\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index + 4] + "\tL");
			}
		}

		// generiere Übergänge nach Gleichung (4)
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 6; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add(ALPHA + "\t" + BETA + "\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tR");
			}
		}
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 7; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add(ALPHA + "\t" + BETA + "\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tL");
			}
		}

		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 6; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add(BETA + "\t" + BETA + "\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tR");
			}
		}
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 7; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add(BETA + "\t" + BETA + "\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tL");
			}
		}

		// generiere Übergänge nach Gleichung(5)
		for (int index = 0; index < compSymbolTable.length; index++) {
			transitionsNew.add(ALPHA + "\t" + ALPHA + "\t" + compSymbolTable[index][2] + "\t" + sigma[index] + "\tR");
			transitionsNew.add(BETA + "\t" + ALPHA + "\t" + compSymbolTable[index][2] + "\t" + sigma[index] + "\tR");
			transitionsNew.add(ALPHA + "\t" + ALPHA + "\t" + compSymbolTable[index][3] + "\t" + sigma[index] + "\tL");
			transitionsNew.add(BETA + "\t" + ALPHA + "\t" + compSymbolTable[index][3] + "\t" + sigma[index] + "\tL");
		}
	}

	private int indexOf(String[] arr, String val) {
		return Arrays.asList(arr).indexOf(val);
	}

	private void modifyInitialSymbol() {
		if (tape.trim().equals("") || tape.equals(null)) {
			throw new IllegalArgumentException("Tape is empty!");
		}
		Tape t = new Tape();
		t.readTapeFromString(tape);

		t.setCurrentSymbol(compSymbolTable[0][0]);

		tape = t.toString();
	}

}

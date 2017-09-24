package construction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class TM2generator {

	private static final int OLD_STATE = 0;
	private static final int NEW_STATE = 1;
	private static final int OLD_SYMBOL = 2;
	private static final int NEW_SYMBOL = 3;
	private static final int DIRECTION = 4;

	private String[][] compSymbolTable;
	private String[] sigma;
	private String[] states;
	private String[] transitions;
	private LinkedList<String> transitionsNew;

	public TM2generator(String[] sigmaA, String[] oldStates, String[] oldTransitions) {
		transitionsNew = new LinkedList<>();
		sigma = Arrays.copyOf(sigmaA, sigmaA.length);
		states = Arrays.copyOf(oldStates, oldStates.length);
		transitions = Arrays.copyOf(oldTransitions, oldTransitions.length);
	}

	public void generateNativeTransitions() {

		for (String trans : transitions) {
			String result = "alpha\t";
			String[] transArray = trans.split(" ");

			int oldSymbolIndex = indexOf(sigma, transArray[OLD_SYMBOL]);
			int oldStateIndex = indexOf(states, transArray[OLD_STATE]);
			int newSymbolIndex = indexOf(sigma, transArray[NEW_SYMBOL]);
			int newStateIndex = indexOf(states, transArray[NEW_STATE]);

			if ("R".equals(transArray[DIRECTION])) {
				try {
					result = result + "beta\t";
					result = result + compSymbolTable[oldSymbolIndex][oldStateIndex * 4] + "\t";
					result = result + compSymbolTable[newSymbolIndex][newStateIndex * 4 + 2] + "\t";
					result = result + "R\t";
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException("invalid transition!");
				}
			} else if ("L".equals(transArray[DIRECTION])) {
				try {
					result = result + "alpha\t";
					result = result + compSymbolTable[oldSymbolIndex][oldStateIndex * 4 + 1] + "\t";
					result = result + compSymbolTable[newSymbolIndex][newStateIndex * 4 + 3] + "\t";
					result = result + "L\t";
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException("invalid transition!");
				}
			} else {
				throw new IllegalArgumentException("invalid transition!");
			}

			transitionsNew.add(result);
		}
	}

	public String[] generateSymbolArrays(String symbol, String[] states) {
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

	public String[][] generateComSymbolTable() {
		compSymbolTable = new String[sigma.length][];

		for (int index = 0; index < sigma.length; index++) {
			compSymbolTable[index] = generateSymbolArrays(sigma[index], oldStates);
		}

		return compSymbolTable;
	}

	public void generateCompTransitions() {

		// generiere Übergänge nach Gleichung (1)
		for (int index = 0; index < sigma.length; index++) {
			transitionsNew.add("alpha\t alpha\t" + sigma[index] + "\t" + compSymbolTable[index][1] + "\tR");
		}

		// generiere Übergänge nach Gleichung (2)
		for (int index = 0; index < sigma.length; index++) {
			transitionsNew.add("beta\t alpha\t" + sigma[index] + "\t" + compSymbolTable[index][2] + "\tL");
		}

		// generiere Übergänge nach Gleichung (3)
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 0; index < compSymbolTable[symbolclass].length - 4; index = index + 4) {
				transitionsNew.add("beta\t alpha\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index + 4] + "\tR");
			}
		}
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 1; index < compSymbolTable[symbolclass].length - 4; index = index + 4) {
				transitionsNew.add("beta\t alpha\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index + 4] + "\tL");
			}
		}

		// generiere Übergänge nach Gleichung (4)
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 4; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add("alpha\t beta\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tR");
			}
		}
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 5; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add("alpha\t beta\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tL");
			}
		}

		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 4; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add("beta\t beta\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tR");
			}
		}
		for (int symbolclass = 0; symbolclass < compSymbolTable.length; symbolclass++) {
			for (int index = 5; index < compSymbolTable[symbolclass].length; index = index + 4) {
				transitionsNew.add("beta\t beta\t" + compSymbolTable[symbolclass][index] + "\t"
						+ compSymbolTable[symbolclass][index - 4] + "\tL");
			}
		}

		// generiere Übergänge nach Gleichung(5)
		for (int index = 0; index < compSymbolTable.length; index++) {
			transitionsNew.add("alpha\t beta\t" + compSymbolTable[index][0] + "\t" + sigma[index] + "\tR");
			transitionsNew.add("beta\t beta\t" + compSymbolTable[index][0] + "\t" + sigma[index] + "\tR");
			transitionsNew.add("alpha\t beta\t" + compSymbolTable[index][1] + "\t" + sigma[index] + "\tL");
			transitionsNew.add("beta\t beta\t" + compSymbolTable[index][1] + "\t" + sigma[index] + "\tL");
		}
	}

	public void printTransitions() {
		for (String string : transitionsNew) {
			System.out.println(string);
		}
	}

	public void print2StateTM() {
		System.out.println("states\nalpha\nbeta\n");
		System.out.println("transitions\n");
		printTransitions();
	}

	public static int indexOf(String[] arr, String val) {
		return Arrays.asList(arr).indexOf(val);
	}

}

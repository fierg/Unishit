package interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import construction.TM2generator;

public class TuringMachine {

	public static final int OLD_STATE = 0;
	public static final int NEW_STATE = 1;
	public static final int OLD_SYMBOL = 2;
	public static final int NEW_SYMBOL = 3;
	public static final int DIRECTION = 4;

	private Tape tape;
	private State currentState;
	private Map<State, HashMap<String, Transition>> transitions;

	public TuringMachine() {
		tape = new Tape();
		transitions = new HashMap<State, HashMap<String, Transition>>();
	}

	public void readTMfromFile(String path) {
		LinkedList<String> states = new LinkedList<>();
		LinkedList<String> transitions = new LinkedList<>();
		String[] symbols = null;

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			for (String line; (line = br.readLine()) != null;) {
				if (TM2generator.STATES.equals(line)) {
					while ((line = br.readLine()) != null && !"transitions".equals(line) && !"".equals(line)) {
						states.add(line);
					}
				} else if (TM2generator.TRANSITIONS.equals(line)) {
					while ((line = br.readLine()) != null && !"symbols".equals(line) && !"".equals(line)) {
						transitions.add(line);
					}
				} else if (TM2generator.SYMBOLS.equals(line)) {
					line = br.readLine();
					symbols = line.split(" ");
				} else if (TM2generator.TAPE.equals(line)) {
					tape.readTapeFromString(br.readLine());
				}
			}

			String[] statesArray = states.toArray(new String[states.size()]);
			String[] sigma = symbols;
			String[] transitionsArray = transitions.toArray(new String[transitions.size()]);

		} catch (IOException e) {
			System.err.println("Failed to read TM from File!");
			e.getMessage();
		}
	}

	public void setTransitionMap(String[] states, String[] transitionsArray) {
		for (String stateName : states) {
			
			stateName.trim();
			transitions.put(new State(stateName), new HashMap<String, Transition>());

		}

		for (String transitionString : transitionsArray) {
			String[] tr = transitionString.split(" ");
			transitions.get(tr[OLD_STATE]).put(tr[OLD_SYMBOL], new Transition(transitions., tr[NEW_SYMBOL], tr[DIRECTION]));
		}

	}

}

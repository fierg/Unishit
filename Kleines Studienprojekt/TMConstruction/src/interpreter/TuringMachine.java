package interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
	private String[] sigma;
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

			this.sigma = symbols;

			String[] statesArray = states.toArray(new String[states.size()]);
			String[] transitionsArray = transitions.toArray(new String[transitions.size()]);

			setTransitionMap(statesArray, transitionsArray);

		} catch (IOException e) {
			System.err.println("Failed to read TM from File!");
			e.getMessage();
		}
	}

	public void setTransitionMap(String[] states, String[] transitionsArray) {
		boolean first = true;
		for (String stateName : states) {

			stateName.trim();
			State s = new State(stateName);
			transitions.put(s, new HashMap<String, Transition>());

			if (first) {
				currentState = s;
				first = false;
			}

			for (String symbol : sigma) {
				transitions.get(s).put(symbol.trim(), null);
			}

		}

		for (String transitionString : transitionsArray) {
			String[] tr = transitionString.split(" ");

			State oldState = null;
			State newState = null;
			boolean foundOldState = false;
			boolean foundNewState = false;
			Iterator<State> i = transitions.keySet().iterator();

			while (i.hasNext()) {
				State curr = i.next();
				if (curr.getName().equals(tr[OLD_STATE].trim())) {
					oldState = curr;
					foundOldState = true;
				}
				if (curr.getName().equals(tr[NEW_STATE].trim())) {
					newState = curr;
					foundNewState = true;
				}
				if (foundNewState && foundOldState) {
					break;
				}
			}

			if (!foundNewState || !foundOldState) {
				System.err
						.println("Error!\nFound old State:\t" + foundOldState + "\nFound new State:\t" + foundNewState);
				throw new IllegalArgumentException();
			}

			if (transitions.get(oldState).get(tr[OLD_SYMBOL].trim()) == null) {
				transitions.get(oldState).put(tr[OLD_SYMBOL],
						new Transition(newState, tr[NEW_SYMBOL].trim(), tr[DIRECTION].trim()));
			} else {
				System.err.println("Current transition:\t" + transitions.get(oldState).get(tr[OLD_SYMBOL]).toString()
						+ "\nNew transition:\t" + new Transition(newState, tr[NEW_SYMBOL], tr[DIRECTION]).toString());
				throw new IllegalArgumentException("TM nondeterministic !!!");

			}

		}

	}
	
	
	public void step() {
		
	}

}

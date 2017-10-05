package interpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import construction.TM2generator;

public class TuringMachine {

	private static final int OLD_STATE = 0;
	private static final int NEW_STATE = 1;
	private static final int OLD_SYMBOL = 2;
	private static final int NEW_SYMBOL = 3;
	private static final int DIRECTION = 4;

	private boolean terminated;
	private boolean debug = true;
	private Tape tape;
	private State currentState;
	private Map<State, HashMap<String, Transition>> transitions;
	private LinkedList<String> history;
	private LinkedList<String> historyDetails;

	public TuringMachine(boolean debug) {
		history = new LinkedList<>();
		historyDetails = new LinkedList<>();
		this.debug = debug;
		terminated = false;
		tape = new Tape();
		transitions = new HashMap<State, HashMap<String, Transition>>();
	}
	
	public void writeTMtoFile(String filename, boolean includeDetails) {
		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
			out.print(getHistory(includeDetails));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getHistory(boolean includeDetails) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (String entry : history) {
			sb.append(entry);
			if(includeDetails) {
				sb.append(historyDetails.get(index++));
			}
		}
		return sb.toString();
	}

	public void readTMfromFile(String path) {
		LinkedList<String> states = new LinkedList<>();
		LinkedList<String> transitions = new LinkedList<>();
//		String[] symbols = null;

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
			/*	} else if (TM2generator.SYMBOLS.equals(line)) {
					line = br.readLine();
					symbols = line.split(" "); */
				} else if (TM2generator.TAPE.equals(line)) {
					tape.readTapeFromString(br.readLine());
				}
			}

			String[] statesArray = states.toArray(new String[states.size()]);
			String[] transitionsArray = transitions.toArray(new String[transitions.size()]);

			setTransitionMap(statesArray, transitionsArray);

		} catch (Exception e) {
			System.err.println("Failed to read TM from File!" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void setTransitionMap(String[] states, String[] transitionsArray) {
		boolean first = true;
		for (String stateName : states) {
			stateName.trim();
			State s = new State(stateName);
			transitions.put(s, new HashMap<String, Transition>());
			if (first) {
				currentState = s;
				first = false;
			}
		}

		for (String transitionString : transitionsArray) {

			String[] tr = transitionString.replace("\t", " ").split(" ");

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

			if (debug) {
				System.out.println("Transition: " + Arrays.toString(tr));
				System.out.println("Map before: " + transitions.get(oldState).entrySet().toString());
			}
			if (transitions.get(oldState).put(tr[OLD_SYMBOL],
					new Transition(newState, tr[NEW_SYMBOL].trim(), tr[DIRECTION].trim())) == null) {
				if (debug) {
					System.out.println("Map after: " + transitions.get(oldState).entrySet().toString());
				}
			} else {
				System.err.println("Current transition:\t" + transitions.get(oldState).get(tr[OLD_SYMBOL]).toString()
						+ "\nNew transition:\t" + new Transition(newState, tr[NEW_SYMBOL], tr[DIRECTION]).toString());
				if (transitions.get(oldState).get(tr[OLD_SYMBOL]).toString()
						.equals(new Transition(newState, tr[NEW_SYMBOL], tr[DIRECTION]).toString())) {
					System.out.println("Transitions matching, should be ok.");
				} else {
					throw new IllegalArgumentException("TM nondeterministic !!!");
				}

			}

		}

	}

	public void step() {
		Transition t = transitions.get(currentState).get(tape.getCurrentSymbol());
		if (t == null) {
			System.err.println(
					"Current state: " + currentState.getName() + "\nCurrent symbol: " + tape.getCurrentSymbol());
			throw new IllegalArgumentException("No existing transition for current state and symbol found!");

		}
		tape.setCurrentSymbol(t.getSymbolOut());
		if (t.getDirection().equals("R")) {
			tape.moveRight();
		} else if (t.getDirection().equals("L")) {
			tape.moveLeft();
		} else {
			System.err.println("Standing still currently not tested...");
		}

		currentState = t.getNewState();
		if (currentState.isFinal()) {
			terminated = true;
		}
	}

	public void runTM(long timeoutMili) {

		if (transitions.get(currentState).isEmpty()) {
			System.out.println("No transitions! unable to simulate TM!\n");
			return;
		}

		System.out.println("Starting TM with Tape:\n");
		System.out.println(tape.toString());
		history.add(tape.toString());

		while (!terminated) {
			historyDetails.add("Current state: " + currentState.getName());
			historyDetails.add("Transition: " + transitions.get(currentState).get(tape.getCurrentSymbol()));
			if (debug) {
				System.out.println("Current state: " + currentState.getName());
				System.out.println("Transition: " + transitions.get(currentState).get(tape.getCurrentSymbol()) + "\n");
			}
			try {
				step();
				TimeUnit.MILLISECONDS.sleep(timeoutMili);
			} catch (IllegalArgumentException | InterruptedException e) {
				historyDetails.add("Failed to simulate TM!\n" + e.getMessage());
				System.err.println("Failed to simulate TM!\n" + e.getMessage());
				break;
			}
			history.add(tape.toString());
			System.out.println(tape.toString());
		}

		if (terminated) {
			String output = "TM terminated!\t";
			if (currentState.isAccepting()) {
				output = output + "TM accepts input";
			} else if (currentState.isDeclining()) {
				output = output + "TM declines input";
			}
			historyDetails.add(output);
			System.out.println(output);
		}

	}

	public void run2StateTM(long timeoutMili) {

		if (transitions.get(currentState).isEmpty()) {
			System.out.println("No transitions! unable to simulate TM!\n");
			return;
		}

		System.out.println("Starting TM with Tape:\n");
		System.out.println(tape.toString());
		while (!terminated) {
			if (debug) {
				System.out.println("Current state: " + currentState.getName());
				System.out.println("Transition: " + transitions.get(currentState).get(tape.getCurrentSymbol()) + "\n");
			}
			try {
				step();
				TimeUnit.MILLISECONDS.sleep(timeoutMili);
			} catch (IllegalArgumentException e) {
				State s = new State(tape.getCurrentSymbol().split(";")[1]);
				if (s.isFinal()) {
					terminated = true;
					break;
				} else {
					System.err.println("Failed to simulate TM!\n" + e.getMessage());
					break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(tape.toString());
		}

		if (terminated) {
			System.out.println("TM terminated!");
			State s = new State(tape.getCurrentSymbol().split(";")[1]);
			if (s.isAccepting()) {
				System.out.println("TM accepts input");
			} else if (s.isDeclining()) {
				System.out.println("TM declines input");
			}
		}

	}

}

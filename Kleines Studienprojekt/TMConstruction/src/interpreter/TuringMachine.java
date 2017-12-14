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

	// statische indizes der Symbole in den Transitionarrays
	private static final int OLD_STATE = 0;
	private static final int NEW_STATE = 1;
	private static final int OLD_SYMBOL = 2;
	private static final int NEW_SYMBOL = 3;
	private static final int DIRECTION = 4;

	private boolean terminated;
	private boolean debug;
	private int nrOfStates = 0;

	// eigentliche TM
	private Tape tape;
	private State currentState;
	private Map<State, HashMap<String, Transition>> transitions;

	// Verlauf, kann nach Terminierung ausgegeben werden
	private LinkedList<String> history;
	private LinkedList<String> historyDetails;

	// Konstruktor
	public TuringMachine(boolean debug) {
		history = new LinkedList<>();
		historyDetails = new LinkedList<>();
		this.debug = debug;
		terminated = false;
		tape = new Tape();
		transitions = new HashMap<State, HashMap<String, Transition>>();
	}

	// schreibt verlauf der TM in Datei
	public void writeHistoryToFile(String filename, boolean includeDetails, boolean tex) {
		try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
			if (tex) {
				out.print(getHistoryAsTex(includeDetails));
			} else {
				out.print(getHistory(includeDetails));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getHistoryAsTex(boolean includeDetails) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		sb.append("\\documentclass[10pt, a4paper]{article}");
		sb.append("\\usepackage[utf8]{inputenc}");
		sb.append("\\usepackage[margin=1in]{geometry}");
		sb.append("\\begin{document}");
		sb.append("\\section*{Output}");

		for (String entry : history) {
			sb.append(entry + "\n");
			if (includeDetails) {
				if (historyDetails.size() > index) {
					sb.append(historyDetails.get(index++) + "\n");
				}
			}
		}

		sb.append("\\end{document}");

		return sb.toString();
	}

	public String getHistory(boolean includeDetails) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (String entry : history) {
			sb.append(entry + "\n");
			if (includeDetails) {
				if (historyDetails.size() > index) {
					sb.append(historyDetails.get(index++) + "\n");
				}
			}
		}
		return sb.toString();
	}

	// ließt TM von Datei ein
	public void readTMfromFile(String path) {
		LinkedList<String> states = new LinkedList<>();
		LinkedList<String> transitions = new LinkedList<>();

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
			System.exit(1);
		}
	}

	/**
	 * @param timeoutMili
	 *            TESTMETHODE
	 */
	public void run(long timeoutMili, boolean texOut) {
		if (this.currentState.equals(null) || this.nrOfStates == 0 || this.tape.equals(null)
				|| this.transitions.equals(null)) {
			throw new IllegalArgumentException("Unable to run this TM!");
		}
		if (nrOfStates == 2) {
			runTM(timeoutMili, true, texOut);
		} else {
			runTM(timeoutMili, false, false);
		}
	}

	// erstellt Übergangs-Map aus eingelesenen Übergängen
	private void setTransitionMap(String[] states, String[] transitionsArray) {
		boolean first = true;
		nrOfStates = states.length;
		for (String stateName : states) {
			stateName.trim();
			State s = new State(stateName);
			transitions.put(s, new HashMap<String, Transition>());
			if (first) {
				currentState = s;
				first = false;
			}
		}

		// jeder Übergang wird zunäachst normiert und anschließend in Map mit übergängen
		// gespeichert
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

			// wenn neuer Übergang noch nicht exisitiert, wird er hinzugefügt
			if (transitions.get(oldState).put(tr[OLD_SYMBOL],
					new Transition(newState, tr[NEW_SYMBOL].trim(), tr[DIRECTION].trim())) == null) {
				if (debug) {
					System.out.println("Map after: " + transitions.get(oldState).entrySet().toString());
				}

				// andernfalls wird überprüft ob neuer Übergang von altem abweicht, falls ja ist
				// TM nicht deterministisch und kann nicht durch diesen Simulator simuliert
				// werden.
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

	// führ einen schritt der TM aus
	private void step() {

		// zu beginn wird Übergang aus Map geladen, falls kein Übergang exisitiert wird
		// abgebrochen.
		Transition t = transitions.get(currentState).get(tape.getCurrentSymbol());
		if (t == null || t.equals(null)) {
			System.out.println(
					"Current state: " + currentState.getName() + "\nCurrent symbol: " + tape.getCurrentSymbol());
			terminated = true;
			throw new NullPointerException("No existing transition for current state and symbol found!");
		}

		// anschließend wird aktueller Schritt vollzogen und das Band bewegt
		tape.setCurrentSymbol(t.getSymbolOut());
		if (t.getDirection().equals("R")) {
			tape.moveRight();
		} else if (t.getDirection().equals("L")) {
			tape.moveLeft();
		} else {
			System.err.println("Standing still currently not tested...");
		}

		currentState = t.getNewState();

		// wenn aktueller Zustand final ist, wird die Simulation beendet.
		if (currentState.isFinal()) {
			terminated = true;
		}
	}

	// Führt eine TM vollstaändig aus
	public void runTM(long timeoutMili, boolean twoStates, boolean texOutput) {

		if (transitions.get(currentState).isEmpty()) {
			System.out.println("No transitions! unable to simulate TM!\n");
			return;
		}

		System.out.println("Starting TM with Tape:");
		System.out.println(tape.toString() + "\n\n");
		history.add(tape.toString());

		// Solange TM nicht terminiert, werden einzeln Schritte vollzogen und der
		// Verlauf gespeichert
		while (!terminated) {
			historyDetails.add("Current state: " + currentState.getName() + "\t Transition: \t"
					+ transitions.get(currentState).get(tape.getCurrentSymbol()));
			if (debug) {
				System.out.println("Current state: " + currentState.getName());
				System.out.println("Transition: " + transitions.get(currentState).get(tape.getCurrentSymbol()) + "\n");
			}
			try {
				step();
				TimeUnit.MILLISECONDS.sleep(timeoutMili);
			} catch (IllegalArgumentException | InterruptedException | NullPointerException e) {
				if (!(e instanceof NullPointerException)) {
					historyDetails.add("Failed to simulate TM!\n" + e.getMessage());
					System.err.println("Failed to simulate TM!\n" + e.getMessage());
					break;
				}
			}
			if (!twoStates) {
				history.add(tape.toString());
				System.out.println(tape.toString());
			} else {
				history.add(tape.toString2States(texOutput));
				System.out.println(tape.toString2States(texOutput));
			}

		}

		// Nachdem TM terminiert, wird entschieden ob eingabe akzeptiert oder abgelehnt
		// wird
		String output = "TM terminated!\t";

		if (twoStates) {
			// in der TM mit 2 Zuständen befindet sich diese Information im aktuellen Symbol
			// und muss erst extrahiert werden.
			State s = new State(tape.getCurrentSymbol().split(";")[1]);
			if (s.isAccepting()) {
				output += "TM accepts input";
			} else if (s.isDeclining()) {
				output += "TM declines input";
			} else {
				output += "TM seems to have failed. There was no next step possible due to missing transition.";
			}

		} else {
			if (currentState.isAccepting()) {
				output = output + "TM accepts input";
			} else if (currentState.isDeclining()) {
				output = output + "TM declines input";
			} else {
				output += "TM seems to have failed. There was no next step possible due to missing transition.";
			}
		}

		history.add(output);
		System.out.println(output);

	}

}

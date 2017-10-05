package application;

import java.util.concurrent.TimeUnit;

import construction.TM2generator;
import interpreter.TuringMachine;

public class Application {

	public static void main(String[] args) {
		boolean debug = false;
		boolean print2File = false;
		boolean printDetails = false;
		boolean convert = false;
		long delayMilis = 0;

		if (args.length < 1) {
			System.err.println(
					"Usage: <TM as .tur file> <milis delay> (optional delay between tape jumps) \n-p (print simulation history to file) \n-pd (print also details) \n-c (convert to 2 State TM before simulating) \n-d (debug)");
			System.exit(1);
		}

		if (args[1].chars().allMatch(Character::isDigit)) {
			delayMilis = Long.parseLong(args[1]);
		}

		String filename = args[0];
		if (contains(args, "-d")) {
			debug = true;
		}
		if (contains(args, "-p")) {
			print2File = true;
		}
		if (contains(args, "-pd")) {
			printDetails = true;
		}
		if (contains(args, "-c")) {
			convert = true;
			System.out.println("Init 2 State TM Generator...\n");
			TM2generator gen = new TM2generator(args[0]);
			gen.generate2StateTM();
			if (debug) {
				gen.printTransitions();
			}
			filename = args[0].split(".tur")[0] + "_2S.tur";
			System.out.println("\n\nwriting 2 State TM to file " + filename);
			gen.writeTM2toFile(filename);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("reading TM from file\t" + filename);
		
		TuringMachine tm = new TuringMachine(debug);
		tm.readTMfromFile(filename);
		
		System.out.println("starting simulator\n");
		if (convert) {
			tm.run2StateTM(delayMilis);
		} else {
			tm.runTM(delayMilis);
		}

		if (print2File) {
			System.out.println("printing history to file:\t" + filename.split(".tur")[0] + ".history");
			tm.writeTMtoFile(filename.split(".tur")[0] + ".history", printDetails);
		}

	}

	private static boolean contains(String[] arr, String key) {
		for (String string : arr) {
			if (key.equals(string)) {
				return true;
			}
		}
		return false;
	}
}

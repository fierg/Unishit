package construction;

public class TestClass {

	private static String[] oldSymbols = { "0", "1", "X", "#" };
	private static String[] oldStates = { "q0", "q1", "q2", "q3", "q4", "q5" };
	private static String[] oldTransitions = { "q0 q0 0 0 L", "q0 q0 1 1 L", "q0 q0 X X L", "q0 q1 # # R",
			"q1 q2 0 X R", "q1 q4 1 X R", "q1 q0 X X R", "q1 q5 # # L", "q2 q2 0 0 R", "q2 q0 1 X L", "q2 q0 X X R",
			"q2 q3 # # R", "q4 q1 0 X L", "q4 q4 1 1 R", "q4 q4 X X R", "q4 q3 # # R", };

	public static void main(String[] args) {
//		TM2generator gen = new TM2generator(oldSymbols, oldStates, oldTransitions);
		
		TM2generator gen = new TM2generator("tur/equal01.tur");
		gen.generateTransitions();
		gen.printTransitions();
		gen.writeTM2toFile("tur/equal01_2S.tur");

	}
}

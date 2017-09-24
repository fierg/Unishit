import java.util.HashMap;
import java.util.Map;

public class TuringMachine {
	
	private Tape tape;
	private Map<String, HashMap<String, String>> transitions;

	public TuringMachine() {
		tape = new Tape();
		transitions = new HashMap<String,HashMap<String,String>>();
	}
	
}

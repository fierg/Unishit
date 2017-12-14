package interpreter;

/*
 * Kapselt Zustände der TM und bestimmt ob Zustand final/akzeptierend/ablehnend
 */
public class State {

	private String name;
	private boolean isFinal = false;
	private boolean isAccepting = false;
	private boolean isDeclining = false;

	public State(String name) {
		this.name = name;
		if (name.endsWith("a")) {
			setAccepting();
		} else if (name.endsWith("d")) {
			setDeclining();
		} else if (name.endsWith("f")) {
			setFinal(true);
		}
	}

	public static String getNameAsTex(String name) {
		try {
		String[] symbolArray = name.split(";");

		if (symbolArray.length > 2) {
			StringBuilder sb = new StringBuilder();
			if(Tape.LATEX_ESCAPE_SYMBOLS.contains(symbolArray[0])) {
				sb.append("\\");
				sb.append(symbolArray[0]);
			} else {
				sb.append(symbolArray[0]);
			}
			sb.append("_{");
			sb.append(symbolArray[1]);
			sb.append(",");
			sb.append(symbolArray[2]);
			sb.append(",");
			sb.append(symbolArray[3]);
			sb.append("}");
			
			return sb.toString();
		}
		}catch (NullPointerException e) {
		}
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
		if (!isFinal) {
			isAccepting = false;
			isDeclining = false;
		}
	}

	public boolean isAccepting() {
		return isAccepting;
	}

	public void setAccepting() {
		this.isAccepting = true;
		this.isFinal = true;
		if (isDeclining) {
			isDeclining = false;
		}
	}

	public boolean isDeclining() {
		return isDeclining;
	}

	public void setDeclining() {
		this.isDeclining = true;
		this.isFinal = true;
		if (isAccepting) {
			isAccepting = false;
		}
	}

}

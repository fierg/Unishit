package interpreter;

public class State {

	private String name;
	private boolean isFinal = false;
	private boolean isAccepting = false;
	private boolean isDeclining = false;
	
	

	public State(String name) {
		this.name = name;
		if(name.endsWith("a")){
			setAccepting(true);
		} else if(name.endsWith("d")){
			setDeclining(true);
		} else if(name.endsWith("f")){
			setFinal(true);
		}
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

	public void setAccepting(boolean isAccepting) {
		this.isAccepting = isAccepting;
		if (isDeclining) {
			isDeclining = false;
		}
	}

	public boolean isDeclining() {
		return isDeclining;
	}

	public void setDeclining(boolean isDeclining) {
		this.isDeclining = isDeclining;
		if (isAccepting) {
			isAccepting = false;
		}
	}

}

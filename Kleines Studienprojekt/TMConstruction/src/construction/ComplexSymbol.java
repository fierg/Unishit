package construction;

/**
 * Boilerplate-Code für die zusätzlich erstellten Symbole beim Umwandeln einer TM
 */
public class ComplexSymbol {
	
	public static final String INFORMATION_PLUS = "+";
	public static final String INFORMATION_MINUS = "-";
	public static final String DIRECTION_R = "R";
	public static final String DIRECTION_L = "L";
	
	private String symbol;
	private String state;
	private String information;
	private String direction;

	public ComplexSymbol(String symbol, String state, String information, String direction) {
		this.symbol = symbol;
		this.state = state;
		this.information = information;
		this.direction = direction;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String toString(){
		return symbol + ";" + state + ";" + information + ";" + direction;
	}
}

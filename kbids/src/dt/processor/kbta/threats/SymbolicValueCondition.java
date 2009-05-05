package dt.processor.kbta.threats;

import java.util.ArrayList;

public class SymbolicValueCondition {
	private final ArrayList<String> _symbolicValueConditions;

	public SymbolicValueCondition(ArrayList<String> symbolicValueConditions) {
		_symbolicValueConditions = symbolicValueConditions;

	}

	public boolean check(String elementValue) {
		for (String st : _symbolicValueConditions) {
			if (st.equals(elementValue)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String st = "";

		st += "symbolicValueCondition ";
		for (String s : _symbolicValueConditions) {
			st += s + " ";

		}
		return st;
	}

}

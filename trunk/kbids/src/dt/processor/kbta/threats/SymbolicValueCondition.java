package dt.processor.kbta.threats;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Element;

public class SymbolicValueCondition {
	private final ArrayList<String> _symbolicValueConditions;

	public SymbolicValueCondition(ArrayList<String> symbolicValueConditions) {
		_symbolicValueConditions = symbolicValueConditions;

	}

	public boolean checkValueCondition(Element element) {
		// long elementValue=element.getValue;
		// for(Duration duration : _durationConditions){
		// if(elementDuration>=duration.getMin() &&
		// elementDuration<=duration.getMax()){
		// return true;
		// }
		// }
		// TODO there is getValue for state,trend, primitive
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

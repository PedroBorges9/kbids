package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;

public class GeneratedFromPattern extends GeneratedFrom {

	public GeneratedFromPattern(String name,
			SymbolicValueCondition symbolicValueCondition,
			DurationCondition durationCondition) {
		super(name, symbolicValueCondition, durationCondition);
		
	}

	
	@Override
	public String toString() {
		String st="";
		st += "< Pattern"  + " name=" + _name + ">\n";
		st += _symbolicValueCondition;
		st += "\n";
		st += _durationCondition;

		return st;
	}


	@Override
	public boolean matchConditions(AllInstanceContainer allInstances) {
		// TODO Auto-generated method stub
		return false;
	}
}

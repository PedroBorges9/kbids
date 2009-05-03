package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;

public class GeneratedFromTrend extends GeneratedFrom {

	public GeneratedFromTrend(String name,
			SymbolicValueCondition symbolicValueCondition,
			DurationCondition durationCondition) {
		super(name, symbolicValueCondition, durationCondition);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		String st="";
		st += "< Trend"  + " name=" + _name + ">\n";
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

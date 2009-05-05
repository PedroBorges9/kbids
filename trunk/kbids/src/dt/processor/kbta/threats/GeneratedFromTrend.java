package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;

public class GeneratedFromTrend extends GeneratedFrom{

	public GeneratedFromTrend(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);
	}

	@Override
	public boolean matchConditions(AllInstanceContainer allInstances){
		// TODO Implement threat generation from trends
		return false;
	}

	@Override
	public String toString(){
		return "Trend: " + super.toString();
	}
}

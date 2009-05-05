package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;

public final class GeneratedFromPattern extends GeneratedFrom{

	public GeneratedFromPattern(String name,
		SymbolicValueCondition symbolicValueCondition, DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);
	}

	@Override
	public boolean matchConditions(AllInstanceContainer allInstances){
		// TODO Implement threat generation from patterns
		return false;
	}

	@Override
	public String toString(){
		return "Pattern: " + super.toString();
	}

}

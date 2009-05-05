package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Element;

public class GeneratedFromTrend extends GeneratedFrom{

	public GeneratedFromTrend(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);
	}

	@Override
	public Element locateMatchingElement(AllInstanceContainer allInstances){
		// TODO Implement threat generation from trends
		return null;
	}

	@Override
	public String toString(){
		return "Trend: " + super.toString();
	}
}

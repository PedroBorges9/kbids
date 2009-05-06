package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Element;

public final class GeneratedFromPattern extends GeneratedFrom{

	public GeneratedFromPattern(String name,
		SymbolicValueCondition symbolicValueCondition, DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);
	}

	@Override
	public Element locateMatchingElement(AllInstanceContainer allInstances){
		// TODO Implement threat generation from patterns
		return null;
	}

	@Override
	public String toString(){
		return "Pattern: " + super.toString();
	}

}
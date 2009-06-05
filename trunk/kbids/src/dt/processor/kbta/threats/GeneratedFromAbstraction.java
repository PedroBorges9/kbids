package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Abstraction;
import dt.processor.kbta.ontology.instances.Element;

public abstract class GeneratedFromAbstraction extends GeneratedFrom{

	public GeneratedFromAbstraction(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);

	}

	protected abstract Abstraction getCurrentAbstraction(AllInstanceContainer allInstances, String name);
	
	@Override
	public Element locateMatchingElement(AllInstanceContainer allInstances){
		Abstraction abstraction = getCurrentAbstraction(allInstances, _elementName);
		if (abstraction == null){
			return null;
		}

		String value = abstraction.getValue();
		long dur = abstraction.getTimeInterval().getDuration();
		if ((_symbolicValueCondition == null ? true : _symbolicValueCondition
				.check(value))
			&& _durationCondition.check(dur)){
			return abstraction;
		}else{
			return null;
		}
	}
}

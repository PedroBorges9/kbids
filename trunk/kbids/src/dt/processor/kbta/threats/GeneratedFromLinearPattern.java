package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.LinearPattern;

public final class GeneratedFromLinearPattern extends GeneratedFrom{

	public GeneratedFromLinearPattern(String name,
		SymbolicValueCondition symbolicValueCondition, DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);
	}

	@Override
	public Element locateMatchingElement(AllInstanceContainer allInstances){
		ComplexContainer<LinearPattern> linearPatterns = allInstances.getLinearPatterns();
		LinearPattern pattern = linearPatterns.getCurrentElement(_elementName);
		if (pattern == null){
			return null;
		}

		// We assume the pattern has no value and so ignore the value condition
		long dur = pattern.getTimeInterval().getDuration();
		return _durationCondition.check(dur) ? pattern : null;
	}

	@Override
	public String toString(){
		return "LinearPattern: " + super.toString();
	}

	@Override
	public String getElementDefDescription(){
		return " type=LinearPattern " + "name=" + _elementName;
	}

	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getLinearPatternDef(_elementName);
	}
}

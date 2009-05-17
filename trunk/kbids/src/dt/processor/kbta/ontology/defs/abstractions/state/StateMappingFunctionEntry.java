package dt.processor.kbta.ontology.defs.abstractions.state;

import java.util.HashMap;

import dt.processor.kbta.ontology.instances.Element;

public class StateMappingFunctionEntry{
	private final String _mappedValue;

	private final HashMap<AbstractedFrom, ElementCondition> _conditions;

	public StateMappingFunctionEntry(String value,
		HashMap<AbstractedFrom, ElementCondition> elementCondition){
		_mappedValue = value;
		_conditions = elementCondition;
	}

	public String mapElements(Element[] abstractedFrom){
		// Checking that each of the elements passes it's matching condition
		for (Element af : abstractedFrom){
			// We assume that af is not null and it has a matching condition in the entry
			ElementCondition elementCondition = _conditions.get(af);
			if (!elementCondition.checkValue(af)){
				// One of the elements' value doesn't match the condition
				// so the mapping has failed
				return null;
			}
		}

		// All of the checks have passed so we return the mapped value
		return _mappedValue;
	}

	@Override
	public String toString(){
		String st = "value=" + _mappedValue + "\n";
		for (ElementCondition ec : _conditions.values()){
			st += ec + "\n";
		}
		return st;
	}

}

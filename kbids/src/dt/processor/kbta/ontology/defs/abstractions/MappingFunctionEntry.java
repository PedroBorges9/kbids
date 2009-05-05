package dt.processor.kbta.ontology.defs.abstractions;

import java.util.HashMap;

import dt.processor.kbta.ontology.instances.Element;

public class MappingFunctionEntry {
	private final String _value;
	private final HashMap<AbstractedFrom, ElementCondition> _elementCondition;

	public MappingFunctionEntry(String value,
		 HashMap<AbstractedFrom, ElementCondition> elementCondition) {
		_value = value;
		_elementCondition = elementCondition;
	}

	public String getValue() {
		return _value;
	}

	
	public String mapElements(Element[] abstractedFrom) {
		for(Element af : abstractedFrom){
			ElementCondition elementCondition=_elementCondition.get(af);
			if(!elementCondition.checkValue(af)){
				return null;
			}
		}

		return _value;
	}

	@Override
	public String toString() {
		String st = "value=" + _value + "\n";
		for (ElementCondition ec : _elementCondition.values()) {
			st += ec + "\n";
		}
		return st;
	}

}

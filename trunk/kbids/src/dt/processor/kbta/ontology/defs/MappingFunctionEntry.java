package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;

public class MappingFunctionEntry{
	private final String _value;
	private final ArrayList<ElementCondition> _elementCondition;
	
	public MappingFunctionEntry(String value,ArrayList<ElementCondition> elementCondition) {
		_value = value;
		_elementCondition = elementCondition;
	}
	
	@Override
	public String toString() {
		String st="value="+_value+"\n";
		for(ElementCondition ec : _elementCondition){
			st+=ec+"\n";
		}
		return st;
	}
	

}

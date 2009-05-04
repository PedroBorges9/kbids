package dt.processor.kbta.ontology.defs.abstractions;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.State;

public class StateCondition extends ElementCondition {
	private final String _value;

	public StateCondition(String name,String value) {
		super(name);
		_value = value;
		
	}
	
	public String getValue() {
		return _value;
	}
	
	@Override
	public String toString() {
		return "name="+_name+" value="+_value;
	}

	@Override
	public boolean checkValue(Element element){
		if(element==null){
			return false;
		}
		State state=(State)element;
		return _value.equalsIgnoreCase(state.getValue());
	}

}

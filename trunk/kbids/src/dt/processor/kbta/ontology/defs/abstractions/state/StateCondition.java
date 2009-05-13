package dt.processor.kbta.ontology.defs.abstractions.state;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.State;

public final class StateCondition extends ElementCondition{
	private final String _value;

	public StateCondition(String name, String value){
		super(name);
		_value = value;

	}

	@Override
	public boolean checkValue(Element element){
		if (element == null){
			return false;
		}
		State state = (State)element;
		return _value.equalsIgnoreCase(state.getValue());
	}

	@Override
	public String toString(){
		return "name=" + _name + " value=" + _value;
	}
}

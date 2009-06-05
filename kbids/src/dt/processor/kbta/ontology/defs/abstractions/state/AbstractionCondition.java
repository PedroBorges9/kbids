package dt.processor.kbta.ontology.defs.abstractions.state;

import dt.processor.kbta.ontology.instances.Abstraction;
import dt.processor.kbta.ontology.instances.Element;

public final class AbstractionCondition extends ElementCondition{
	private final String _value;

	public AbstractionCondition(String name, String value){
		super(name);
		_value = value;

	}

	@Override
	public boolean checkValue(Element element){
		if (element == null){
			return false;
		}
		Abstraction abstraction = (Abstraction)element;
		return _value.equalsIgnoreCase(abstraction.getValue());
	}

	@Override
	public String toString(){
		return "name=" + _name + " value=" + _value;
	}
}

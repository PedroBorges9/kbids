package dt.processor.kbta.ontology.defs.abstractions.state;

import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;

public final class PrimitiveCondition extends ElementCondition{
	private final NumericRange _numericRange;

	public PrimitiveCondition(String name, NumericRange numericRange){
		super(name);
		_numericRange = numericRange;
	}

	@Override
	public String toString(){
		return "name=" + _name + " " + _numericRange;
	}

	@Override
	public boolean checkValue(Element element){
		if (element == null){
			return false;
		}
		Primitive primitive = (Primitive)element;
		return _numericRange.isInRange(primitive.getValue());
	}

}

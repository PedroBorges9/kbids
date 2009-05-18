package dt.processor.kbta.ontology.defs.abstractions.state;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Trend;

public class TrendCondition extends ElementCondition{

	private final String _value;

	public TrendCondition(String name, String value){
		super(name);
		_value = value;

	}
	@Override
	public boolean checkValue(Element element){
		if (element == null){
			return false;
		}
		Trend trend = (Trend)element;
		return _value.equalsIgnoreCase(trend.getValue());
	}

	@Override
	public String toString(){
		return "name=" + _name + " value=" + _value;
	}

}

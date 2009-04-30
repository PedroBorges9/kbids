package dt.processor.kbta.ontology.defs;

public class StateCondition extends ElementCondition {
	private final String _value;

	public StateCondition(String name,String value) {
		super(name);
		_value = value;
		
	}
	
	@Override
	public String toString() {
		return "name="+_name+" value="+_value;
	}

}

package dt.processor.kbta.ontology.defs;

public class PrimitiveCondition extends ElementCondition {
	private final NumericRange _numericRange;

	public PrimitiveCondition(String name,NumericRange numericRange) {
		super(name);
		_numericRange = numericRange;
	
	}
	
	@Override
	public String toString() {
		return "name="+_name+" "+_numericRange;
	}

}

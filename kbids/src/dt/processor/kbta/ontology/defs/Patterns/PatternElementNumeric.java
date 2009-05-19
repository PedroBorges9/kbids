package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.threats.DurationCondition;

public class PatternElementNumeric extends PatternElements {
	private NumericRange _value;

	public PatternElementNumeric(int type,String name, int ordinal,
			DurationCondition duration, NumericRange value) {
		super(type,name, ordinal, duration);
		_value = value;
	}
	
}

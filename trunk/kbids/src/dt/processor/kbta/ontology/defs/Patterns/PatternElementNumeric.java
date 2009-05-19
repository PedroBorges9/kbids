package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.threats.DurationCondition;

public class PatternElementNumeric extends PatternElement {
	private NumericRange _numricRange;

	public PatternElementNumeric(int type,String name, int ordinal,
			DurationCondition duration, NumericRange numricRange) {
		super(type,name, ordinal, duration);
		_numricRange = numricRange;
	}
	
}

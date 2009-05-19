package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.threats.DurationCondition;

public class PatternElementSymbolic extends PatternElements {
	public String _value;

	public PatternElementSymbolic(int type, int ordinal,
			DurationCondition duration, String value) {
		super(type, ordinal, duration);
		_value = value;
	}
}

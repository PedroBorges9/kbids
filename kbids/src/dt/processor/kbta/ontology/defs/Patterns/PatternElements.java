package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.threats.DurationCondition;

public class PatternElements {
	private int _type;
	private String _name;
	private int _ordinal;
	private DurationCondition _duration;
	public PatternElements(int type,String name, int ordinal, DurationCondition duration) {
		_type = type;
		_ordinal = ordinal;
		_duration = duration;
		_name = name;
	}
}

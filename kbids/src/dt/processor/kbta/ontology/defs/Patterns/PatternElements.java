package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.threats.DurationCondition;

public class PatternElements {
	private int _type;
	private int _ordinal;
	private DurationCondition _duration;
	public PatternElements(int type, int ordinal, DurationCondition duration) {
		this._type = type;
		this._ordinal = ordinal;
		this._duration = duration;
	}
}

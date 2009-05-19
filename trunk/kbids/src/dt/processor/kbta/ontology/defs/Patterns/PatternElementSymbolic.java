package dt.processor.kbta.ontology.defs.Patterns;

import java.util.HashSet;

import dt.processor.kbta.threats.DurationCondition;

public class PatternElementSymbolic extends PatternElements {
	public HashSet<String> _symbolicValueCondition;

	public PatternElementSymbolic(int type,String name, int ordinal,
			DurationCondition duration, HashSet<String> symbolicValueCondition) {
		super(type,name, ordinal, duration);
		_symbolicValueCondition = symbolicValueCondition;
	}
}

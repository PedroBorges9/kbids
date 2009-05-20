package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.threats.DurationCondition;

public class PatternElement {
	private int _type;
	private String _name;
	private int _ordinal;
	private DurationCondition _duration;
	public PatternElement(int type,String name, int ordinal, DurationCondition duration) {
		_type = type;
		_ordinal = ordinal;
		_duration = duration;
		_name = name;
	}
	
	@Override
	public String toString(){
		return "type= "+_type+" name= "+_name+" ordinal= "+_ordinal+_duration;
	}
}
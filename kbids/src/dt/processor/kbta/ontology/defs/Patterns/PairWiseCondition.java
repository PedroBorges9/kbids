package dt.processor.kbta.ontology.defs.Patterns;


public class PairWiseCondition {
	
	private int _first;
	private int _second;
	private ValueCondition _value;
	private TemporalCondition _temporal;
	
	public PairWiseCondition(int first, int second, ValueCondition value,
			TemporalCondition temporal) {
		_first = first;
		_second = second;
		_value = value;
		_temporal = temporal;
	}
	
	@Override
	public String toString(){	
		return " first= "+_first+" second= "+_second+" value= "+_value+" temporal= "+_temporal;
	}
	
	
}

package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;


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
	
	public int getFirst() {
		return _first;
	}



	public int getSecond() {
		return _second;
	}

	
	@Override
	public String toString(){	
		return " first= "+_first+" second= "+_second+" value= "+_value+" temporal= "+_temporal;
	}

	public boolean obeys(Element element, Element element2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}

package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;
import java.util.Iterator;

import dt.processor.kbta.ontology.instances.Element;


public class PairWiseCondition {

	public static final int DONTCARE=0;   
	public static final int SMALLER=1;
	public static final int SAME=2;
	public static final int BIGGER=3;
	
	
	private int _first;
	private int _second;
	private int _value;
	private TemporalCondition _temporal;

	public PairWiseCondition(int first, int second, int valueCondition,
			TemporalCondition temporal) {
		_first = first;
		_second = second;
		_value = valueCondition;
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

	public boolean obeys(Element element1, Element element2) {
		if (!_temporal.Obeys(element1, element2)){
			return false;
		}
		switch (_value) {
			case DONTCARE:
				return true;
				
			case SMALLER:
				if (element1.compareTo(element2)<0){
					return true;
				}
				break;
			case SAME:
				if (element1.compareTo(element2)==0){
					return true;
				}
				break;
			case BIGGER:
				if (element1.compareTo(element2)>0){
					return true;
				}
				break;
			default:
				break;
			}
			return false;
	}
}

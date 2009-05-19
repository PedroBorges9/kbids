package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;

public class OverlapTemporalCondition extends TemporalCondition {
	
	private DurationCondition _durationLength;
	private DurationCondition _durationStartingDistance;

	public OverlapTemporalCondition(DurationCondition durationLength,
		DurationCondition durationStartingDistance){
		super();
		_durationLength = durationLength;
		_durationStartingDistance = durationStartingDistance;
	}

	@Override
	public boolean Obeys(Element a, Element b) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString(){
		return "Overlap"+_durationLength+_durationStartingDistance;
	}
}

package dt.processor.kbta.ontology.defs.patterns;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;

public class BeforeTemporalCondition extends TemporalCondition{
	private DurationCondition _duration;

	public BeforeTemporalCondition(DurationCondition duration){
		_duration = duration;
	}
	
	@Override
	public boolean check(Element a, Element b){
		return (_duration.check(
				b.getTimeInterval().getStartTime()-a.getTimeInterval().getEndTime()));
	}

	@Override
	public String toString(){
		return "Before"+_duration;
	}
	
}

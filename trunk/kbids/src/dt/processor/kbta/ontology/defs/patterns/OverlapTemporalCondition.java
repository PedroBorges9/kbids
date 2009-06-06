package dt.processor.kbta.ontology.defs.patterns;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.util.TimeInterval;

public class OverlapTemporalCondition extends TemporalCondition{

	private DurationCondition _durationLength;

	private DurationCondition _durationStartingDistance;

	public OverlapTemporalCondition(DurationCondition durationLength,
		DurationCondition durationStartingDistance){
		super();
		_durationLength = durationLength;
		_durationStartingDistance = durationStartingDistance;
	}

	@Override
	public boolean check(Element a, Element b){
		TimeInterval ta = a.getTimeInterval();
		TimeInterval tb = b.getTimeInterval();
		TimeInterval o = ta.getOverlap(tb);
		if (o == null){
			return false;
		}
		long sd = Math.abs(ta.getStartTime() - tb.getStartTime());
		return (_durationLength.check(o.getDuration()) && _durationStartingDistance
				.check(sd));
	}

	@Override
	public String toString(){
		return "Overlap" + _durationLength + _durationStartingDistance;
	}
}

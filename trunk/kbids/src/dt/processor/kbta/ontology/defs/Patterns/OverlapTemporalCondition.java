package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public class OverlapTemporalCondition extends TemporalCondition {
	
	private long _MinLength;
	private long _MaxLength;
	private long _MinStartingDistance;
	private long _MaxStartingDistance;
	
	public OverlapTemporalCondition(long minLength, long maxLength,
			long minStartingDistance, long maxStartingDistance) {
		super();
		_MinLength = minLength;
		_MaxLength = maxLength;
		_MinStartingDistance = minStartingDistance;
		_MaxStartingDistance = maxStartingDistance;
	}
	@Override
	public boolean Obeys(Element a, Element b) {
		// TODO Auto-generated method stub
		return false;
	}
}

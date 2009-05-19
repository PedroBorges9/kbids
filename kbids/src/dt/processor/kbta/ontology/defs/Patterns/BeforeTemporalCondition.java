package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public class BeforeTemporalCondition extends TemporalCondition {
	private long _minGap;
	private long _maxGap;
	public BeforeTemporalCondition(long minGap, long maxGap) {
		super();
		_minGap = minGap;
		_maxGap = maxGap;
	}
	
	@Override
	public boolean Obeys(Element a, Element b) {
		// TODO Auto-generated method stub
		return false;
	}
}

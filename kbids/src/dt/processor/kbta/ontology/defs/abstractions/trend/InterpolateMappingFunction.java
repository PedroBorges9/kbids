package dt.processor.kbta.ontology.defs.abstractions.trend;

public class InterpolateMappingFunction{
	private final double _treshold;

	private final double _angle;

	private final long _maxGap;

	public InterpolateMappingFunction(double treshold, double angle, long maxGap){
		_treshold = treshold;
		_angle = angle;
		_maxGap = maxGap;
	}

	@Override
	public String toString(){
		return "treshold= " + _treshold + " angle= " + _angle + " maxGap=" + _maxGap;
	}

}

package dt.processor.kbta.ontology.defs;

public final class NumericRange{
	private final double _minValue;
	private final double _maxValue;
	private final boolean _isMaxE;
	private final boolean _isMinE;		
	
	public NumericRange(double minValue, double maxValue, boolean isMinE, boolean isMaxE){
		_minValue = minValue;
		_maxValue = maxValue;
		_isMaxE = isMaxE;
		_isMinE = isMinE;
	}

	public boolean isInRange(double value){
		return ((_isMinE) ? value >= _minValue : value > _minValue) 
			&& ((_isMaxE) ? value <= _maxValue : value < _maxValue);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("[");
		sb.append("min");
		if (_isMinE){
			sb.append("E");
		}
		sb.append("=").append(_minValue);
		
		sb.append(", max");
		if (_isMaxE){
			sb.append("E");
		}
		sb.append("=").append(_maxValue).append("]");
		
		return sb.toString(); 
	}
}

package dt.processor.kbta.threats;

import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.InvalidDateException;

public class Duration {
	private final long _min;
	private final long _max;
	
	public Duration(String min, String max) throws InvalidDateException{
		_min = new ISODuration(min).toMillis();
		_max = new ISODuration(max).toMillis();
	}
	
	
	public long getMax() {
		return _max;
	}
	
	public long getMin() {
		return _min;
	}
	
	@Override
	public String toString() {
		
		return "min="+_min+" max="+_max;
	}
	
	
	
	
	
	

}

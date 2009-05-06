package dt.processor.kbta.threats;

import java.text.SimpleDateFormat;

import dt.processor.kbta.util.ISODuration;

public final class DurationCondition{
	private final long _min;

	private final long _max;

	public DurationCondition(String min, String max) throws Exception{
		_min = new ISODuration(min).toMillis();
		_max = new ISODuration(max).toMillis();
	}

	public boolean check(long duration){
		return duration >= _min && duration <= _max;
	}

	@Override
	public String toString(){
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return "Duration=[" + df.format(_min) + ", " + df.format(_max) + "]";
	}

	public long getMinDuration(){
		return _min;
	}
}

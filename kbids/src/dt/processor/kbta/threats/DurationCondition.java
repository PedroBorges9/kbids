package dt.processor.kbta.threats;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.InvalidDateException;

public class DurationCondition {
	private final long _min;
	private final long _max;

	public DurationCondition(String min, String max)
			throws InvalidDateException {

		// System.out.println("minmin="+min+" maxmax="+max);
		_min = new ISODuration(min).toMillis();
		_max = new ISODuration(max).toMillis();
		// System.out.println("minmin2="+_min+" maxmax2="+_max);
	}

	public boolean check(long duration) {
		return duration >= _min && duration <= _max;
	}

	@Override
	public String toString() {
		return "min=" + _min + " max=" + _max;
	}
}

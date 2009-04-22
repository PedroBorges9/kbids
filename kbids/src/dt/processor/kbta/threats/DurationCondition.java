package dt.processor.kbta.threats;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Element;

public class DurationCondition {
	private final ArrayList<Duration> _durationConditions;

	public DurationCondition(ArrayList<Duration> durationConditions) {
		_durationConditions = durationConditions;

	}

	public boolean checkCondition(Element element) {
		long elementDuration = element.getDuration();
		for (Duration duration : _durationConditions) {
			if (elementDuration >= duration.getMin()
					&& elementDuration <= duration.getMax()) {
				return true;
			}
		}
		return false;

	}

	@Override
	public String toString() {
		String st = "durationCondition ";
		for (Duration d : _durationConditions) {
			st += d + "\n";

		}
		return st;
	}

}

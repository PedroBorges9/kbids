package dt.processor.kbta.threats;

import java.util.ArrayList;
import java.util.Collection;

import dt.processor.kbta.container.AllInstanceContainer;

public class ThreatAssessor {
	private final ArrayList<ThreatAssessment> _containerAssessments;

	public ThreatAssessor() {
		_containerAssessments = new ArrayList<ThreatAssessment>();
	}

	public void addThreatAssessment(ThreatAssessment ta) {
		_containerAssessments.add(ta);
	}

	public Collection<ThreatAssessment> assess(AllInstanceContainer allInstances) {
		Collection<ThreatAssessment> assessments = new ArrayList<ThreatAssessment>();

		for (ThreatAssessment ta : _containerAssessments) {
			GeneratedFrom genratedFrom = ta.getGeneratedFrom();

			if (genratedFrom.matchConditions(allInstances)) {
				assessments.add(ta);
			}
		}

		return assessments;
	}

	@Override
	public String toString() {
		String st = "";
		for (ThreatAssessment t : _containerAssessments) {
			st += t + "\n";
		}

		return st;

	}

}

package dt.processor.kbta.threats;

import java.util.*;

import dt.processor.kbta.AllInstanceContainer;
import dt.processor.kbta.ElementContainer;
import dt.processor.kbta.ontology.instances.State;

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

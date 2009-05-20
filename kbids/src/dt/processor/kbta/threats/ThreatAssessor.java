package dt.processor.kbta.threats;

import java.util.ArrayList;
import java.util.Collection;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.util.Pair;

public class ThreatAssessor{
	private final ArrayList<ThreatAssessment> _assessments;

	public ThreatAssessor(){
		_assessments = new ArrayList<ThreatAssessment>();
	}

	public void addThreatAssessment(ThreatAssessment ta){
		_assessments.add(ta);
	}

	public Collection<Pair<ThreatAssessment, Element>> assess(
		AllInstanceContainer allInstances){
		Collection<Pair<ThreatAssessment, Element>> assessments = new ArrayList<Pair<ThreatAssessment, Element>>();

		for (ThreatAssessment ta : _assessments){
			GeneratedFrom gf = ta.getGeneratedFrom();
			Element element = gf.locateMatchingElement(allInstances);
			if (element != null){
				assessments.add(new Pair<ThreatAssessment, Element>(ta, element));
			}
		}

		return assessments;
	}

	public void setInitiallyMonitoredThreats(Ontology ontology){
		for (ThreatAssessment ta : _assessments){
			ta.setInitiallyMonitoredThreat(ontology);
		}
	}

	@Override
	public String toString(){
		String st = "";
		for (ThreatAssessment t : _assessments){
			st += t + "\n";
		}

		return st;

	}

}

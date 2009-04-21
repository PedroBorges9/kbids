package dt.processor.kbta.threats;

import java.util.*;

import dt.processor.kbta.AllInstanceContainer;

public class ThreatAssessor {
	private ArrayList<ThreatAssessment> _containerAssessments;

	public ThreatAssessor() {
		_containerAssessments = new ArrayList<ThreatAssessment>();
	}
	
	public void addThreatAssessment(ThreatAssessment ta){
		_containerAssessments.add(ta);
	}
	
	public Collection<ThreatAssessment> assess(AllInstanceContainer allInstances){
		Collection<ThreatAssessment> assessments = new ArrayList<ThreatAssessment>();
		
		//TODO go over the instances and do something...
		
		return assessments;
	}
	
	@Override
	public String toString() {
		String st="";
		for(ThreatAssessment t : _containerAssessments){
			st+=t.toString()+"\n";
		}
		
		return st;
		
	}
	
	
}

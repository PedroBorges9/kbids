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
	
	public void addThreatAssessment(ThreatAssessment ta){
		_containerAssessments.add(ta);
	}
	
	public Collection<ThreatAssessment> assess(AllInstanceContainer allInstances){
		Collection<ThreatAssessment> assessments = new ArrayList<ThreatAssessment>();
		
		for(ThreatAssessment ca : _containerAssessments ){
			GeneratedFrom gf=ca.getGeneratedFrom();
			String type=gf.getType();
			String name=gf.getName();
			
			if (type.equalsIgnoreCase("State")){
				ElementContainer<State> states=allInstances.getStates();

//				ArrayList<State> stateListForName=states.getElements(name);
//				stateListForName.get(stateListForName.size()-1);
				
				State state= states.getMostRecent(); 
				String stateValue=state.getValue();
				
				
				//TODO compare Duration, and need to check all States or the last;Match condition
				if(gf.symbolicValueConditionExist(stateValue)){
					
					
				}
				
	
			}else if (type.equalsIgnoreCase("Trend")){
				
			}else if (type.equalsIgnoreCase("Pattern")){
				
			}
			
			
			
			
		}
		                                               
		
		
		
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

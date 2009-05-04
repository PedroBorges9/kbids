package dt.processor.kbta.ontology.defs.abstractions;

import java.util.HashMap;
import java.util.Map;

import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.util.TimeInterval;

public class InterpolationFunction {
	private HashMap<String, Long> _interpolationFunction;

	public InterpolationFunction(
			HashMap<String, Long> interpolationFunction) {
		_interpolationFunction = interpolationFunction;
	}
	
	

	@Override
	public String toString() {
		String st="";
		for(Map.Entry<String,Long > entry : _interpolationFunction.entrySet()){
			st+="name="+entry.getKey()+" "+entry.getValue()+"\n";
		}
		return st;
	}


	public boolean interpolate(State currentState, String value, TimeInterval timeInterval){
		String valueCurrentState=currentState.getValue();
		if(valueCurrentState.equalsIgnoreCase(value)){
			long maxGap=_interpolationFunction.get(value);
			TimeInterval currentTimeInterval=currentState.getTimeInterval();
			if((timeInterval.getStartTime()-currentTimeInterval.getEndTime())<=maxGap){
				currentTimeInterval.setEndTime(timeInterval.getEndTime());
				return true;
			}
					
		}
		
		return false;
	}
	
	
	

}

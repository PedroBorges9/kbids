package dt.processor.kbta.ontology.defs.abstractions.state;

import java.util.HashMap;
import java.util.Map;

import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.util.TimeInterval;

public class InterpolationFunction{
	private final HashMap<String, Long> _interpolationFunction;

	public InterpolationFunction(HashMap<String, Long> interpolationFunction){
		_interpolationFunction = interpolationFunction;
	}

	public boolean interpolate(State before, String valueAfter, TimeInterval timeIntervalAfter){
		String valueBefore = before.getValue();
		
		if (valueBefore.equalsIgnoreCase(valueAfter)){
			long maxGap = _interpolationFunction.get(valueAfter);
			
			TimeInterval timeIntervalBefore = before.getTimeInterval();
			if ((timeIntervalAfter.getStartTime() - timeIntervalBefore.getEndTime()) <= maxGap){
				timeIntervalBefore.setEndTime(timeIntervalAfter.getEndTime());
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString(){
		String st = "";
		for (Map.Entry<String, Long> entry : _interpolationFunction.entrySet()){
			st += "name=" + entry.getKey() + " " + entry.getValue() + "\n";
		}
		return st;
	}
	
}

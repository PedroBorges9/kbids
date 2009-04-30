package dt.processor.kbta.ontology.defs;

import java.util.HashMap;
import java.util.Map;

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
	
	
	

}

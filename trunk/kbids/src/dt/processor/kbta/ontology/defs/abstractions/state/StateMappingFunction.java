package dt.processor.kbta.ontology.defs.abstractions.state;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Element;

public class StateMappingFunction{
	private final StateMappingFunctionEntry[] _mappingFunction;

	public StateMappingFunction(ArrayList<StateMappingFunctionEntry> mappingFunction){
		_mappingFunction = mappingFunction
				.toArray(new StateMappingFunctionEntry[mappingFunction.size()]);
	}

	public String mapElements(Element[] abstractedFrom){
		// Checking for each possible mapped value (an entry in the function)
		// whether the given elements can be abstracted into it
		for (StateMappingFunctionEntry mfe : _mappingFunction){
			String value = mfe.mapElements(abstractedFrom);
			if (value != null){
				// Stopping if a match was found
				return value;
			}
		}
		// The elements couldn't be abstracted into either of the values
		return null;
	}

	@Override
	public String toString(){
		String st = "";
		for (StateMappingFunctionEntry mfe : _mappingFunction){
			st += mfe + "\n";
		}
		return st;
	}

}

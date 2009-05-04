package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Element;

public class MappingFunction {
	private final MappingFunctionEntry[] _mappingFunction;
	
	public MappingFunction(ArrayList<MappingFunctionEntry> mappingFunction) {
		_mappingFunction = mappingFunction.toArray(new MappingFunctionEntry[mappingFunction.size()]);
	}
	
	public String mapElements(Element[] abstractedFrom) {
		for(MappingFunctionEntry mfe : _mappingFunction){
			String value = mfe.mapElements(abstractedFrom);
			if(value !=null){
				return value;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		String st="";
		for(MappingFunctionEntry mfe : _mappingFunction){
			st+=mfe+"\n";
		}
		return st;
	}

	

}

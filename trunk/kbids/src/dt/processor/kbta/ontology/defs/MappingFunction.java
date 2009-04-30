package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;

public class MappingFunction {
	private final ArrayList<MappingFunctionEntry> _mappingFunction;
	
	public MappingFunction(ArrayList<MappingFunctionEntry> mappingFunction) {
		_mappingFunction = mappingFunction;
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

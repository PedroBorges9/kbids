/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;

/**
 * @author
 * 
 */
public class StateDef extends AbstractionDef {
	private final ArrayList<AbstractedFrom> _abstractedFrom;
	private final ArrayList<String> _necessaryContexts;
	private final MappingFunction _mappingFunction;
	private final InterpolationFunction _interpolationFunction;

	public StateDef(String name, ArrayList<AbstractedFrom> abstractedFrom,
			ArrayList<String> necessaryContexts,
			MappingFunction mappingFunction,
			InterpolationFunction interpolationFunction) {
		super(name);
		_abstractedFrom = abstractedFrom;
		_necessaryContexts = necessaryContexts;
		_mappingFunction = mappingFunction;
		_interpolationFunction = interpolationFunction;

		
	}
	
	@Override
	public String toString() {
		String st="name="+_name+"\n";
		st+=  " AbstractedFrom\n"+_abstractedFrom+"\n";
		st+=  " NecessaryContexts\n"+_necessaryContexts+"\n";
		st+=  _mappingFunction;
		st+=  _interpolationFunction;
		
		 
		return st;
	}

}

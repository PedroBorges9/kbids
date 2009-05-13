/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions.trend;

import java.util.ArrayList;
import java.util.Arrays;

import dt.processor.kbta.ontology.defs.abstractions.AbstractionDef;

/**
 * @author
 */
public final class TrendDef extends AbstractionDef{
	private final String _abstractedFrom;

	private final InterpolateMappingFunction _mappingFunction;

	public TrendDef(String name, String abstractedFrom,
		ArrayList<String> necessaryContexts, InterpolateMappingFunction mappingFunction){
		super(name, necessaryContexts);
		_abstractedFrom = abstractedFrom;
		_mappingFunction = mappingFunction;
	}

	@Override
	public String toString(){
		return "AbstractedFrom(primitive)= " + _abstractedFrom + "\n"
				+ "necessaryContexts " +  Arrays.toString(_necessaryContexts) + "\n" + "mappingFunction "
				+ _mappingFunction;
	}
}

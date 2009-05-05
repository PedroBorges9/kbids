/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;


/**
 * @author 
 *
 */
public final class TrendDef extends AbstractionDef {

	public TrendDef(String name, ArrayList<AbstractedFrom> abstractedFrom,
		ArrayList<String> necessaryContexts, MappingFunction mappingFunction,
		InterpolationFunction interpolationFunction){
		super(name, abstractedFrom, necessaryContexts, mappingFunction, interpolationFunction);
	}
}

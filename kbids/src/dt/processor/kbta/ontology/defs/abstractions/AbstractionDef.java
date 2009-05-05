/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;

import dt.processor.kbta.ontology.defs.ElementDef;

/**
 * @author
 */
public abstract class AbstractionDef extends ElementDef{

	protected final AbstractedFrom[] _abstractedFrom;

	protected final String[] _necessaryContexts;

	protected final MappingFunction _mappingFunction;

	protected final InterpolationFunction _interpolationFunction;

	public AbstractionDef(String name, ArrayList<AbstractedFrom> abstractedFrom,
		ArrayList<String> necessaryContexts, MappingFunction mappingFunction,
		InterpolationFunction interpolationFunction){
		super(name);
		_abstractedFrom = abstractedFrom
				.toArray(new AbstractedFrom[abstractedFrom.size()]);
		_necessaryContexts = necessaryContexts.toArray(new String[necessaryContexts
				.size()]);
		_mappingFunction = mappingFunction;
		_interpolationFunction = interpolationFunction;
	}

}

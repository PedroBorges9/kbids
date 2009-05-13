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


	protected final String[] _necessaryContexts;

	public AbstractionDef(String name, ArrayList<String> necessaryContexts){
		super(name);
		_necessaryContexts = necessaryContexts.toArray(new String[necessaryContexts
				.size()]);
	}

}

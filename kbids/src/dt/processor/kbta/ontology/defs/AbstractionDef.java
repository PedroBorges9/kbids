/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Context;

/**
 * @author 
 *
 */
public class AbstractionDef extends ElementDef{

	protected String abstracteFrom;
	//TODO represent PersistenceFunction
	//TODO represent MappingFunction
	protected ArrayList<Context> necessaryContext;
	
	public AbstractionDef(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}

/**
 * 
 */
package dt.processor.kbta.ontology;

import java.util.ArrayList;

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

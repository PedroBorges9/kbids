/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author 
 *
 */
public class Context extends Element {

	public Context( String name,TimeInterval timeInterval) {
		super( name, timeInterval);
	}
	
	@Override
	public String toString(){
		return "Context "+_name+" time interval "+_timeInterval;
	}

}

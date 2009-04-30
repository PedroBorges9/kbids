/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;

/**
 * @author 
 *
 */
public class Context extends Element {

	public Context( String name, long start, long end) {
		super( name, start, end);
	}
	
	@Override
	public String toString(){
		return "Context "+_name+" starts at "+_start+" and ends at "+_end;
	}

}

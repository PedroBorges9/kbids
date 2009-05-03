/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author rahamime
 *
 */
public class Abstraction extends Element {
	protected final String _value;
	
	public Abstraction( String name, String value, TimeInterval timeInterval) {
		super( name,timeInterval);
		_value = value;
	}
	
	
	public String getValue() {
		return _value;
	}
	

}

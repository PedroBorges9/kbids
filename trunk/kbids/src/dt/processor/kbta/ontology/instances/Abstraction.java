/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;

/**
 * @author rahamime
 *
 */
public class Abstraction extends Element {
	protected final String _value;
	
	public Abstraction(ElementDef elementDef, String name, String value, long start, long end) {
		super(elementDef, name, start, end);
		_value = value;
	}
	
	
	public String getValue() {
		return _value;
	}
	

}

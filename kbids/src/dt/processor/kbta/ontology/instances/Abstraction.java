/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author rahamime
 *
 */
public abstract class Abstraction extends Element {
	protected final String _value;
	
	public Abstraction(int type, String name, String value, TimeInterval timeInterval) {
		super(type, name,timeInterval);
		_value = value;
	}
	
	
	public String getValue() {
		return _value;
	}
	

}

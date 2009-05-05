/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class State extends Abstraction{

	public State(String name, String value, TimeInterval timeInterval){
		super(STATE, name, value, timeInterval);
	}

}

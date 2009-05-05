/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author 
 *
 */
public class State extends Abstraction {
	

	public State(  String name, String value, TimeInterval timeInterval) {
		super(Element.STATE, name, value,timeInterval);
	}
	

	@Override
	public String toString(){
		return super.toString() + " value= "+_value;
	}


}

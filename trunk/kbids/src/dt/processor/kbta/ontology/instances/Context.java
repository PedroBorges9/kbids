/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author 
 *
 */
public class Context extends Element {

	public Context( String name,TimeInterval timeInterval) {
		super(Element.CONTEXT, name, timeInterval);
	}
	
	@Override
	public String toString(){
		return "Context "+super.toString();
	}

}

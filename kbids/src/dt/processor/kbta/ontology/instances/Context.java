/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Context extends Element{
	public Context(String name, TimeInterval timeInterval){
		super(CONTEXT, name, timeInterval);
	}
}

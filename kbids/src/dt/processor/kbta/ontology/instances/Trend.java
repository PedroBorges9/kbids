/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Trend extends Abstraction{

	public Trend(String name, String value, TimeInterval timeInterval){
		super(TREND, name, value, timeInterval);
	}

}

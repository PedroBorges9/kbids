/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.util.TimeInterval;

/**
 * @author 
 *
 */
public class Trend extends Abstraction {

	public Trend(  String name, String value, TimeInterval timeInterval) {
		super(Element.TREND, name, value,timeInterval);
	}

}

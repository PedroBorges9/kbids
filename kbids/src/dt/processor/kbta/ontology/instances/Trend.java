/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Trend extends Abstraction{

	public Trend(String name, String value, TimeInterval timeInterval, Bundle extras){
		super(TREND, name, value, timeInterval, extras);
	}
}

/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class State extends Abstraction{

	public State(String name, String value, TimeInterval timeInterval, Bundle extras){
		super(STATE, name, value, timeInterval, extras);
	}
}

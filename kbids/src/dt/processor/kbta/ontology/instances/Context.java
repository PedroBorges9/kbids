/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Context extends Element{
	public Context(String name, TimeInterval timeInterval){
		super(CONTEXT, name, timeInterval, null);
	}	
}

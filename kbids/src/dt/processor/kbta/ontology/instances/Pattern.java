/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.HashMap;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;




/**
 * @author 
 *
 */
public final class Pattern extends Element {
	
	public Pattern(  String name, TimeInterval timeInterval) {
		super(PATTERN, name, timeInterval, null);
	}

	@Override
	public int compareTo(Element another) {
		return 0;
	}
}
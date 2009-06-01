/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;

/**
 * @author rahamime
 */
public final class Event extends Element{

	public Event(String name, long start, long end){
		super(EVENT, name, start, end, null);
	}

	@Override
	public int compareTo(Element another) {
		return 0;
	}
}
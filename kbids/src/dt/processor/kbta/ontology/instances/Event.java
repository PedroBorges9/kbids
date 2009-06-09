/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.List;
import java.util.Map;

import android.os.Bundle;


/**
 * @author rahamime
 */
public final class Event extends Element{

	public Event(String name, long start, long end, Bundle extras){
		super(EVENT, name, start, end, extras);
	}

	@Override
	public int compareTo(Element another) {
		return 0;
	}

	@Override
	protected Map toNetProtectElement(){
		return null;
	}
	
	@Override
	public void toNetProtectElement(List<Map> elements){
		// Not sending events to NetProtect
	}
}

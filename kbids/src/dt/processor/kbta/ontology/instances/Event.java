/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.Date;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.util.TimeInterval;

import android.os.Bundle;

/**
 * @author rahamime
 *
 */
public class Event extends Element {
	
	public Event(  String name, TimeInterval timeInterval) {
		 super(Element.EVENT, name, timeInterval);
	}
	
	@Override
	public String toString(){	
		return "Event "+" name= "+_name+" start= "+_timeInterval.getStartTime()+" end= "+_timeInterval.getEndTime();
	}
}

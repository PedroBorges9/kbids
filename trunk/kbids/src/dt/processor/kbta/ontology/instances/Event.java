/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.Date;

import dt.processor.kbta.ontology.defs.ElementDef;

import android.os.Bundle;

/**
 * @author rahamime
 *
 */
public class Event extends Element {
	
	public Event(ElementDef elementDef, String name, long start, long end) {
		 super(elementDef, name, start, end);
	}
	
	@Override
	public String toString(){	
		return "Event "+" name= "+_name+" start= "+_start+" end= "+_end;
	}
}

/**
 * 
 */
package dt.processor.kbta.ontology.instances;


/**
 * @author rahamime
 *
 */
public class Event extends Element {
	
	public Event(  String name, long start, long end) {
		 super(EVENT, name, start, end);
	}
	
	@Override
	public String toString(){	
		return "Event "+" name= "+_name+" start= "+_timeInterval.getStartTime()+" end= "+_timeInterval.getEndTime();
	}
}

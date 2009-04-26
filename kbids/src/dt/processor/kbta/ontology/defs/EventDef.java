/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import dt.fe.MonitoredData;
import dt.processor.kbta.ontology.instances.Event;

/**
 * @author 
 *
 */
public class EventDef extends ElementDef{
	private long _latestEventTime;
	
	public EventDef(String name){
		super(name);
		_latestEventTime=0;
	}

	public Collection<Event> defineEvents(MonitoredData m) {
		Collection<Event> events=new ArrayList<Event>();
		long[] eventTimes = m.getExtras().getLongArray("Events");
		
		if (eventTimes == null || eventTimes.length == 0){
			return null;
		}
		
		for(int i=eventTimes.length-1; i>=0;--i){
			long eventTime=eventTimes[i];
			if(eventTime<=_latestEventTime){
				break;
			}

			Event e= new Event(_name, eventTime,eventTime);
			events.add(e);			
		}
		
		_latestEventTime = eventTimes[eventTimes.length - 1];
		
		return events;
	}
	
	@Override
	public String toString() {
		
		return "name "+_name;
	}
	

}

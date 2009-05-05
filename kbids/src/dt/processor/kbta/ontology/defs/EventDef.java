/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.os.Bundle;

import dt.fe.MonitoredData;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public class EventDef extends ElementDef{
	private long _latestEventTime;

	public EventDef(String name){
		super(name);
		_latestEventTime = 0;
	}

	public Collection<Event> defineEvents(MonitoredData m){
		Collection<Event> events = new ArrayList<Event>();
		Bundle bundle = m.getExtras();
		long[] eventTimes;

		if (bundle == null || (eventTimes = bundle.getLongArray("Events")) == null
				|| eventTimes.length == 0){
			return null;
		}

		for (int i = eventTimes.length - 1; i >= 0; --i){
			long eventTime = eventTimes[i];
			if (eventTime <= _latestEventTime){
				break;
			}

			Event e = new Event(_name, new TimeInterval(eventTime, eventTime));
			events.add(e);
		}

		_latestEventTime = eventTimes[eventTimes.length - 1];

		return events;
	}

	@Override
	public String toString(){

		return "name " + _name;
	}

}

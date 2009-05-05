/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import android.os.Bundle;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Event;

/**
 * @author
 */
public class EventDef extends ElementDef{
	private long _latestEventTime;

	public EventDef(String name){
		super(name);
		_latestEventTime = 0;
	}

	public final void createEvents(Bundle extras, AllInstanceContainer allInstances){		
		long[] eventTimes;

		if (extras == null || (eventTimes = extras.getLongArray("Events")) == null
				|| eventTimes.length == 0){
			return;
		}
		
		for (int i = eventTimes.length - 1; i >= 0; --i){
			long eventTime = eventTimes[i];
			if (eventTime <= _latestEventTime){
				break;
			}

			Event event = new Event(_name, eventTime, eventTime);
			allInstances.addEvent(event);
		}

		_latestEventTime = eventTimes[eventTimes.length - 1];
	}

	@Override
	public String toString(){

		return "name " + _name;
	}

}

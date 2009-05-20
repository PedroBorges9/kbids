/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
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
		
		ArrayList<Event> createdEvents = new ArrayList<Event>(eventTimes.length - 1);
		for (int i = eventTimes.length - 1; i >= 0; --i){
			long eventTime = eventTimes[i];
			if (eventTime <= _latestEventTime){
				break;
			}

			Event event = new Event(_name, eventTime, eventTime);
			createdEvents.add(event);
		}
		Collections.reverse(createdEvents); //FIXME do it normally
		for (Event event : createdEvents){
			allInstances.addEvent(event);
		}

		_latestEventTime = eventTimes[eventTimes.length - 1];
	}
	
	public void setInitiallyIsMonitored(Ontology ontology,boolean monitored){
		_isMonitored = monitored;
		_counter += (_isMonitored ? 1 : 0);
	}
	
	public void setIsMonitored(Ontology ontology,boolean monitored){
		_isMonitored = monitored;
		_counter += (_isMonitored ? 1 : (_counter > 0 ? -1 : 0));
	}


	@Override
	public String toString(){
		return "<Event name=" + _name+" isMonitored="+_isMonitored+" counter="+_counter+"/>";
	}

}

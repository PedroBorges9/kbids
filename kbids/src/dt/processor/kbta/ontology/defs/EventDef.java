package dt.processor.kbta.ontology.defs;

import android.os.Bundle;
import android.os.Parcelable;
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
//		System.out.println("KBTA whole extras: " + extras);
//		if (extras.containsKey("Attributes")){
//			Bundle[] wtf = (Bundle[])extras.getParcelableArray("Attributes");
//			System.out.println(wtf);
//			
//		}
		Parcelable[] eventAttributes = extras.getParcelableArray("Attributes");
//		System.out.println("KBTA: " + ((eventAttributes == null) ? "null" : Arrays.toString(eventAttributes)));
		for (int i = 0; i < eventTimes.length; ++i){
			long eventTime = eventTimes[i];
			if (eventTime <= _latestEventTime){
				continue;
			}
			Bundle attributes = (eventAttributes != null && i < eventAttributes.length) ? 
					(Bundle)eventAttributes[i] : null;
			Event event = new Event(_name, eventTime, eventTime, attributes);
			allInstances.addEvent(event);
		}

		_latestEventTime = eventTimes[eventTimes.length - 1];
	}
	
	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		visitor.visit(this);
		// No one to traverse :)		
	}
	
	@Override
	public String toString(){
		return "Event: " + _name;
	}

}

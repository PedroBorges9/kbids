package dt.processor.kbta.ontology.defs.context;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.util.TimeInterval;

public class EventInduction extends Induction{

	public EventInduction(String elementName, String contextName){
		super(elementName, contextName);
	}

	@Override
	public boolean induce(AllInstanceContainer container){
		ArrayList<Event> events = container.getEvents().getCurrentEvents(_elementName);
		boolean createdContexts = false;
		if (events != null){
			for (Event e : events){
				TimeInterval ti = e.getTimeInterval();
				long start = ti.getStartTime();
				long end = (_relativeToStart ? start : ti.getEndTime()) + _gap;
//				android.util.Log.d("System.out", "Creating context with: " + new TimeInterval(start, end));
				createdContexts = createdContexts | createContext(container, start, end, null);
			}
		}
		return createdContexts;
	}

}

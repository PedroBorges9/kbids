package dt.processor.kbta.ontology.defs.context;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Event;

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
				// We use the fact that for events the start and end
				// time is the same and avoid checking _relativeToStart
				long start = e.getTimeInterval().getEndTime();
				long end = getEndTime(start);
				createdContexts = createdContexts
						| createContext(container, start, end, null);
			}
		}
		return createdContexts;
	}

	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getEventDef(_elementName);
	}

	@Override
	public String getElementDefDescription(){
		return " type=Event " + "name=" + _elementName;
	}

}

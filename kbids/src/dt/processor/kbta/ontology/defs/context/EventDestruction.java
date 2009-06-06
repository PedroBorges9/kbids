package dt.processor.kbta.ontology.defs.context;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Event;

public class EventDestruction extends Destruction{

	public EventDestruction(String elementName, String contextName){
		super(elementName, contextName);
	}

	@Override
	public boolean destroy(AllInstanceContainer container){
		ComplexContainer<Context> cc = container.getContexts();
		Context c = cc.getCurrentElement(_contextName);
		if (c == null){
			return false;
		}
		ArrayList<Event> events = container.getEvents().getCurrentEvents(_elementName);
		if (events != null){
			Event e = events.get(0);
			if (c != null){
				c.getTimeInterval().setEndTime(e.getTimeInterval().getEndTime());
				cc.removeCurrentElement(_contextName);
				cc.addToOld(c, _contextName);
				return true;
			}
		}
		return false;
	}

}

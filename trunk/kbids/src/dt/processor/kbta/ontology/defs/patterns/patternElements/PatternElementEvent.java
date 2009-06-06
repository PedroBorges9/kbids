package dt.processor.kbta.ontology.defs.patterns.patternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.EventContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.threats.DurationCondition;

public final class PatternElementEvent extends PatternElement{

	public PatternElementEvent(int type, String name, int ordinal,
		DurationCondition duration){
		super(type, name, ordinal, duration);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Element> getValidElements(AllInstanceContainer aic){
		ArrayList<Element> ans = new ArrayList<Element>();
		EventContainer ec = aic.getEvents();
		ArrayList<Event>[] events = new ArrayList[]{ec.getOldEvents(_name), ec.getCurrentEvents(_name)};
		for (ArrayList<Event> eArray : events){
			if (eArray != null){
				for (Event e : eArray){
					if (check(e)){
						ans.add(e);
					}
				}
			}
		}

		if (ans.isEmpty()){
			return null;
		}
		return ans;
	}

	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getEventDef(_name);
	}

}

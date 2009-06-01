package dt.processor.kbta.ontology.defs.Patterns.PatternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.EventContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.threats.DurationCondition;

public class PatternElementEvent extends PatternElement {

	public PatternElementEvent(int type, String name, int ordinal,
			DurationCondition duration) {
		super(type, name, ordinal, duration);
	}

	@Override
	public ArrayList<Element> getValid(AllInstanceContainer aic) {
		ArrayList<Element> ans=new ArrayList<Element>();
		EventContainer ec=aic.getEvents();
		ArrayList<Event> eArray;
		eArray=ec.getOldEvent(_name);
		if (eArray!=null){
			for (Event e: eArray){
				if (e!=null ){
					if (obeys(e)){
						ans.add(e);
					}
					
					
				}
			}
		}
		eArray=ec.getCurrentEvents(_name);
		for (Event e: eArray){
			if (e!=null && obeys(e)){
				ans.add(e);
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

package dt.processor.kbta;

import dt.processor.kbta.ontology.*;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.ontology.instances.Pattern;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.ontology.instances.Trend;

public class AllInstanceContainer {
	private final ElementContainer<Primitive> _primitives;
	private final ElementContainer<Event> _events;
	private final ElementContainer<Context> _contexts;
	private final ElementContainer<State> _states;
	private final ElementContainer<Trend> _trends;
	private final ElementContainer<Pattern> _patterns;
	
	public AllInstanceContainer() {
		_primitives = new ElementContainer<Primitive>();;
		_events = new ElementContainer<Event>();
		_contexts = new ElementContainer<Context>();
		_states = new ElementContainer<State>();
		_trends = new ElementContainer<Trend>();
		_patterns = new ElementContainer<Pattern>();
	}
	
	public void addPrimitive(Primitive element){
		_primitives.addElement(element);
	}
	
	public void addEvent(Event element){
		_events.addElement(element);
	}
	
	public void addContext(Context element){
		_contexts.addElement(element);
	}
	
	public void addPattern(Pattern element){
		_patterns.addElement(element);
	}
	
	public void addState(State element){
		_states.addElement(element);
	}
	
	public void addTrend(Trend element){
		_trends.addElement(element);
	}	
}

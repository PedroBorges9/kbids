package dt.processor.kbta.container;

import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.ontology.instances.LinearPattern;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.ontology.instances.Trend;

public class AllInstanceContainer {
	private final PrimitiveContainer _primitives;
	private final EventContainer _events;
	private final ComplexContainer<Context> _contexts;
	private final ComplexContainer<State> _states;
	private final ComplexContainer<Trend> _trends;
	private final ComplexContainer<LinearPattern> _patterns;
	
	public AllInstanceContainer() {
		_primitives = new PrimitiveContainer();
		_events = new EventContainer();
		_contexts = new ComplexContainer<Context>();
		_states = new ComplexContainer<State>();
		_trends = new ComplexContainer<Trend>();
		_patterns = new ComplexContainer<LinearPattern>();
	}
	
	public void addPrimitive(Primitive element){
		_primitives.addPrimitive(element);
	}
	
	public void addEvent(Event element){
		_events.addEvent(element);
	}
	
	public void addContext(Context element){
		System.out.println(element);
	}
	
	public void addPattern(LinearPattern element){
		_patterns.addElement(element);
	}
	
	public void addState(State element){
		_states.addElement(element);
	}
	
	public void addTrend(Trend element){
		_trends.addElement(element);
	}

	public PrimitiveContainer getPrimitives() {
		return _primitives;
	}

	public EventContainer getEvents() {
		return _events;
	}

	public ComplexContainer<Context> getContexts() {
		return _contexts;
	}

	public ComplexContainer<State> getStates() {
		return _states;
	}

	public ComplexContainer<Trend> getTrends() {
		return _trends;
	}

	public ComplexContainer<LinearPattern> getLinearPatterns() {
		return _patterns;
	}

	public boolean hasNew() {
		return (_contexts.hasNew() || _states.hasNew() || _trends.hasNew());
	}

	public void shiftBackAll() {
		_primitives.shiftBack();
		_contexts.shiftBack();
		_events.shiftBack();
		_states.shiftBack();
		_trends.shiftBack();
		_patterns.shiftBack();
	}	
	
	public void discardElementsNotWithinRange(long time){
		long current= System.currentTimeMillis();
		long cutOffTime=current-time;
		_primitives.discardOlderThan(cutOffTime);
		_contexts.discardOlderThan(cutOffTime);
		_events.discardOlderThan(cutOffTime);
		_patterns.discardOlderThan(cutOffTime);
		_states.discardOlderThan(cutOffTime);
		_trends.discardOlderThan(cutOffTime);
	}
	
	
}

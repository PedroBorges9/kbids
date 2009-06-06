package dt.processor.kbta.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dt.processor.kbta.ontology.instances.Event;

public final class EventContainer implements ElementContainer{
	private final HashMap<String, ArrayList<Event>> _oldElements;

	private final HashMap<String, ArrayList<Event>> _currentElements;

	public EventContainer(){
		_oldElements = new HashMap<String, ArrayList<Event>>();
		_currentElements = new HashMap<String, ArrayList<Event>>();
	}

	public void addEvent(Event event){
		String name = event.getName();
		ArrayList<Event> events = _currentElements.get(name);
		if (events == null){
			events = new ArrayList<Event>();
			_currentElements.put(name, events);
		}
		events.add(event);
	}

	public ArrayList<Event> getCurrentEvents(String name){
		return _currentElements.get(name);
	}

	public ArrayList<Event> getOldEvents(String name){
		return _oldElements.get(name);
	}

	public void shiftBack(){
		for (Map.Entry<String, ArrayList<Event>> currentEvent : _currentElements
				.entrySet()){
			String name = currentEvent.getKey();
			ArrayList<Event> old = _oldElements.get(name);
			if (old == null){
				old = new ArrayList<Event>();
				_oldElements.put(name, old);
			}
			old.addAll((currentEvent.getValue()));
		}
		_currentElements.clear();
	}

	@Override
	public void discardOlderThan(long time){
		for (Map.Entry<String, ArrayList<Event>> currentEvents : _currentElements
				.entrySet()){
			Iterator<Event> iterator = currentEvents.getValue().iterator();
			while (iterator.hasNext()){
				Event currentEvent = iterator.next();
				if (currentEvent.getTimeInterval().getEndTime() < time){
					iterator.remove();
				}else{
					break;
				}
			}
		}
		for (Map.Entry<String, ArrayList<Event>> oldEvents : _oldElements.entrySet()){
			Iterator<Event> iterator = oldEvents.getValue().iterator();
			while (iterator.hasNext()){
				Event currentEvent = iterator.next();
				if (currentEvent.getTimeInterval().getEndTime() < time){
					iterator.remove();
				}else{
					break;
				}
			}
		}

	}

	@Override
	public String toString(){
		return "Current: " + _currentElements.values() + "\n" + "Old:"
				+ _oldElements.values();
	}

}

package dt.processor.kbta.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dt.processor.kbta.ontology.instances.Event;

public class EventContainer implements ElementContainer{
	private final HashMap<String, ArrayList<Event>> _oldElements;
	private final HashMap<String, ArrayList<Event>> _currentElements; 
	
	public EventContainer(){
		_oldElements = new HashMap<String, ArrayList<Event>>();
		_currentElements = new HashMap<String, ArrayList<Event>>();
		
		// TODO Auto-generated constructor stub
	}
	

	public void addEvent(Event p){
		String name = p.getName();
		ArrayList<Event> events=_currentElements.get(name);
		if (events==null){
			events=new ArrayList<Event>();
			_currentElements.put(name, events);
		}
		events.add(p);
		
	}
	
	public ArrayList<Event> getCurrentEvent(String st){
		return _currentElements.get(st);	 
	}
	
	public ArrayList<Event> getOldEvent(String st){
		return _oldElements.get(st);	 
	}
	
	public Event getRecentEvent(String st){
		ArrayList<Event> old=_oldElements.get(st);
		
		if (old!=null && !old.isEmpty()){
			return old.get(old.size()-1);
		}
		return null;
	}
	
	public void shiftBack(){
		for (Map.Entry<String, ArrayList<Event>> currentEvent: _currentElements.entrySet()){
			String name=currentEvent.getKey();
			ArrayList<Event> old=_oldElements.get(name);
			if (old==null){
				old=new ArrayList<Event>();
				_oldElements.put(name, old);
			}
			old.addAll((currentEvent.getValue()));
		}
		_currentElements.clear();
	}


	@Override
	public void discardOlderThen(long time){
		for (Map.Entry<String, ArrayList<Event>> currentEvents: _currentElements.entrySet()){
			Iterator<Event> iterator=currentEvents.getValue().iterator();
			while(iterator.hasNext()){
				Event currentEvent = iterator.next();
				if (currentEvent.getTimeInterval().getEndTime()<time){
					iterator.remove();
				}
				else{
					break;
				}
			}			
		}
		for (Map.Entry<String, ArrayList<Event>> oldEvents: _oldElements.entrySet()){
			Iterator<Event> iterator=oldEvents.getValue().iterator();
			while(iterator.hasNext()){
				Event currentEvent = iterator.next();
				if (currentEvent.getTimeInterval().getEndTime()<time){
					iterator.remove();
				}
				else{
					break;
				}
			}
		}
			
		
		
	}
	
	

}

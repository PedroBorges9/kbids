package dt.processor.kbta.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dt.processor.kbta.ontology.instances.Element;

public class ComplexContainer <T extends Element> implements ElementContainer{
	private final HashMap<String, ArrayList<T>> _oldElements;
	private final HashMap<String, T> _newElements;
	private final HashMap<String, T> _currentElements; 
	public ComplexContainer() {
		_oldElements = new HashMap<String, ArrayList<T>>();
		_newElements = new HashMap<String, T>();
		_currentElements = new HashMap<String, T>();
	}

	public void addElement(T element){
		String name = element.getName();
		_newElements.put(name, element);
	}

	public ArrayList<T> getOldElements(String st){
		return _oldElements.get(st);
	}

	public T getNewestElement(String st){
		return _newElements.get(st);	 
	}
	
	public T getCurrentElement(String st){
		return _currentElements.get(st);
	}
	
	public void setNewestElement(T t){
		_newElements.put(t.getName(), t);
	}
		
	public void shiftBack(){

		for (Map.Entry<String, T> newElement: _newElements.entrySet()){
			String name=newElement.getKey();
			T current=_currentElements.get(name);
			if (current!=null){
				addToOld(current, name);
			}
			_currentElements.put(name, newElement.getValue());
		}
		_newElements.clear();
	}

	@Override
	public void discardOlderThan(long time){
		Iterator<Map.Entry<String, T>> iter=_currentElements.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, T> currentElement = iter.next();
			String name=currentElement.getKey();
			if (currentElement.getValue().getTimeInterval().getEndTime()<time){
				_oldElements.remove(name);
				iter.remove();
			}
		}
		for (Map.Entry<String, ArrayList<T>> oldElements: _oldElements.entrySet()){
			Iterator<T> iterator=oldElements.getValue().iterator();
			while(iterator.hasNext()){
				T currentElement = iterator.next();
				if (currentElement.getTimeInterval().getEndTime()<time){
					iterator.remove();
				}
				else{
					break;
				}
			}
		}
	}

	public void removeCurrentElement(String name){
		_currentElements.remove(name);
		
	}
	
	public void addToOld(T oldElement, String name){
		ArrayList<T> old=_oldElements.get(name);
		if (old==null){
			old=new ArrayList<T>();
			_oldElements.put(name, old);
		}
		old.add(oldElement);
	}
	
	@Override
	public String toString(){
		return 
		"New: "+_newElements.values()+"\n" +
		"Current: "+_currentElements.values()+"\n" +
		"Old:"+_oldElements.values();
	}

	public boolean hasNew() {
		return !_newElements.isEmpty();
	}
	
	public void clear(){
		_oldElements.clear();
		_newElements.clear();
		_currentElements.clear();
	}
}

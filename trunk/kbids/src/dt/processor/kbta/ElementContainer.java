package dt.processor.kbta;

import java.util.*;

import dt.processor.kbta.ontology.instances.Element;

public class ElementContainer <T extends Element> {
	private final HashMap<String, ArrayList<T>> _elements;
	private final HashMap<String, ArrayList<T>> _newElements;
	private final HashMap<String, ArrayList<T>> _transitionalElements; 
	public ElementContainer() {
		_elements = new HashMap<String, ArrayList<T>>();
		_newElements = new HashMap<String, ArrayList<T>>();
		_transitionalElements = new HashMap<String, ArrayList<T>>();
	}
	
	public void addElement(T element){
		String name = element.getName();
		ArrayList<T> elements = _newElements.get(name);
		if (elements == null){
			elements = new ArrayList<T>();
			_newElements.put(name, elements);
		}
		
		elements.add(element);		
	}
	
	public ArrayList<T> getElements(String st){
		ArrayList<T> elements=_elements.get(st);
		if (elements!=null)
			elements.addAll(_newElements.get(st));
		return elements;
	}
	
	public T getMostRecent(String st){
		ArrayList<T> elementsForName=_newElements.get(st);	 
		
		if(elementsForName==null || elementsForName.size()==0){
			ArrayList<T> TransElementsForName=_transitionalElements.get(st);
			if(TransElementsForName==null || TransElementsForName.size()==0){
				return null;
			}
			return TransElementsForName.get(elementsForName.size()-1);
			
		}
		return elementsForName.get(elementsForName.size()-1);
	}
	
	public ArrayList<T> getNewElements(String st){
		return _newElements.get(st);
	}
	
	public ArrayList<T> getTransElements(String st){
		return _transitionalElements.get(st);
	}
	
	public void CheckAllUsed(){
		for (Map.Entry<String, ArrayList<T>> s: _newElements.entrySet()){
			String key=s.getKey();
			ArrayList<T> elements=_transitionalElements.get(key);
			if (elements==null)	elements =new ArrayList<T>();
			elements.addAll(s.getValue());
			_transitionalElements.put(key, elements);
			
		}
		_newElements.clear();
	}
	
	public void checkTransitional(long time){
		ArrayList<String> remove=new ArrayList<String>();
		for (Map.Entry<String, ArrayList<T>> s: _transitionalElements.entrySet()){
			String key=s.getKey();
			ArrayList<T> elements=s.getValue();
			if (elements!=null){
				for (T t:elements){
					if (t.getEnd()<time){
						remove.add(key);
						_elements.get(key).add(t);
					}
				}
			}
		}
		for (String str: remove){
			_transitionalElements.remove(str);
		}
		
	}
	
	@Override
	public String toString(){
		
		return "Element Container\n Contains Elements: "+_elements+"\nContains new Elements: "+_newElements;
	}
}

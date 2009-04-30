package dt.processor.kbta;

import java.util.*;

import dt.processor.kbta.ontology.instances.Element;

public class ElementContainer <T extends Element> {
	private final HashMap<String, ArrayList<T>> _elements;
	private final HashMap<String, ArrayList<T>> _newElements;
	public ElementContainer() {
		_elements = new HashMap<String, ArrayList<T>>();
		_newElements = new HashMap<String, ArrayList<T>>();
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
			return null;
		}
		
		return elementsForName.get(elementsForName.size()-1);
	}
	public ArrayList<T> getNewElements(String st){
		return _newElements.get(st);
	}
	
	@Override
	public String toString(){
		
		return "Element Container\n Contains Elements: "+_elements+"\nContains new Elements: "+_newElements;
	}
}

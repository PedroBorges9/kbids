package dt.processor.kbta;

import java.util.*;

import dt.processor.kbta.ontology.instances.Element;

public class ElementContainer <T extends Element> {
	private final HashMap<String, ArrayList<T>> _elements;
	
	public ElementContainer() {
		_elements = new HashMap<String, ArrayList<T>>();
	}
	
	public void addElement(T element){
		String name = element.getName();
		ArrayList<T> elements = _elements.get(name);
		if (elements == null){
			elements = new ArrayList<T>();
			_elements.put(name, elements);
		}
		
		elements.add(element);		
	}
}

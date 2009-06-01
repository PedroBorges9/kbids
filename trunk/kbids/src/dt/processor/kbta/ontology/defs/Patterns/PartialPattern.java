package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public class PartialPattern {
	Element[] _elements;

	
	public PartialPattern(int size) {
		_elements=new Element[size];
	}
	
	public PartialPattern(Element[] clone) {
		_elements=clone;
	}

	public Element getElement(int ordinal){
		return _elements[ordinal];
	}
	
		
	public PartialPattern addElement(int ordinal, Element element){
		Element[] newElements=_elements.clone();
		newElements[ordinal]=element;
		return new PartialPattern(newElements);
	}
}

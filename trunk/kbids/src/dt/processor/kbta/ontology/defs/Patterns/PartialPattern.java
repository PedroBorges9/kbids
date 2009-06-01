package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Pattern;
import dt.processor.kbta.util.TimeInterval;

public class PartialPattern {
	Element[] _elements;

	
	public PartialPattern(int size, Element e) {
		_elements=new Element[size];
		_elements[0]=e;
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

	private TimeInterval getTimeInterval(){
		long start=Long.MAX_VALUE;
		long end=0;
		for (Element e: _elements){
			TimeInterval eti=e.getTimeInterval();
			long startTime = eti.getStartTime();
			if (startTime<start){
				start=startTime;
			}
			long endTime = eti.getEndTime();
			if (endTime>end){
				end=endTime;
			}
		}
		return new TimeInterval(start,end);
	}

	public Pattern toPattern(ArrayList<Element>[] elements, String name){
		for (int i=0; i<_elements.length; i++){
			if (_elements[i]==null){
				ArrayList<Element> e=elements[i];
				_elements[i]=e.get(e.size()-1);
			}
		}
		return new Pattern(name, getTimeInterval());
	}

	public PartialPattern addTwoElements(int firstOrdinal, Element e1, 
		int secondOrdinal, Element e2){
		Element[] newElements=_elements.clone();
		newElements[firstOrdinal]=e1;
		newElements[secondOrdinal]=e2;		
		return new PartialPattern(newElements);
	}
}

package dt.processor.kbta.ontology.defs.patterns;

import java.util.ArrayList;

import android.os.Bundle;

import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Pattern;
import dt.processor.kbta.util.TimeInterval;

public class PartialPattern{
	private final Element[] _elements;

	public PartialPattern(int numOfElements, int initElementOrdinal, Element initElement){
		_elements = new Element[numOfElements];
		_elements[0] = initElement;
	}

	public PartialPattern(Element[] elements){
		_elements = elements;
	}

	public Element getElement(int ordinal){
		return _elements[ordinal];
	}
	
	/**
	 * Creates an actual pattern from this partial pattern while filling the blanks using the given valid elements
	 * @param validElements Used to fill the blanks (the elements that had no PWCs associated with them)
	 * @param name The name of the pattern to be created
	 * @return The created pattern
	 */
	public Pattern toPattern(ArrayList<Element>[] validElements, String name){
		Bundle newExtras = new Bundle();
		long start = Long.MAX_VALUE;
		long end = 0;

		for (int i = 0; i < _elements.length; ++i){
			Element e;
			// 1. Filling the blanks (the elements that had no PWCs associated with them)
			if ((e = _elements[i]) == null){
				ArrayList<Element> elements = validElements[i];
				e = elements.get(elements.size() - 1);
				_elements[i] = e;
			}		

			// 2. Collecting the extras from all of the pattern elements
			_elements[i].addInnerExtras(newExtras);

			// 3. Computing the time interval of the pattern
			TimeInterval eti = e.getTimeInterval();
			long startTime = eti.getStartTime();
			if (startTime < start){
				start = startTime;
			}
			long endTime = eti.getEndTime();
			if (endTime != Long.MAX_VALUE && endTime > end){
				end = endTime;
			}
		}
		return new Pattern(name, new TimeInterval(start, end), newExtras);
	}	

	/**
	 * Returns a partial pattern identical to this one with the given element added to it.
	 * <br><b>Note!</b> This partial pattern remains unchanged
	 * 
	 * @param ordinal The ordinal at which to add the new element
	 * @param element The element to be added to the partial pattern
	 * @return A partial pattern identical to this one with the given element added to it
	 */
	public PartialPattern addElement(int ordinal, Element element){
		Element[] newElements = _elements.clone();
		newElements[ordinal] = element;
		return new PartialPattern(newElements);
	}

	/**
	 * Returns a partial pattern identical to this one with the given elements added to it.
	 * <br><b>Note!</b> This partial pattern remains unchanged
	 * 
	 * @param firstOrdinal The ordinal at which to add the first element
	 * @param e1 The first element to be added to the partial pattern
	 * @param secondOrdinal The ordinal at which to add the second element
	 * @param e2 The second element to be added to the partial pattern
	 * @return A partial pattern identical to this one with the given elements added to it
	 */
	public PartialPattern addTwoElements(int firstOrdinal, Element e1, int secondOrdinal,
		Element e2){
		Element[] newElements = _elements.clone();
		newElements[firstOrdinal] = e1;
		newElements[secondOrdinal] = e2;
		return new PartialPattern(newElements);
	}
}

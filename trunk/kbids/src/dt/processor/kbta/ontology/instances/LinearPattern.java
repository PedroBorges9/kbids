package dt.processor.kbta.ontology.instances;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

public final class LinearPattern extends Element{

	private final Element[] _elements;

	public LinearPattern(String name, TimeInterval timeInterval, Bundle extras, Element[] elements){
		super(LINEAR_PATTERN, name, timeInterval, extras);
		_elements = elements;
	}

	@Override
	public int compareTo(Element another){
		return 0;
	}

	@Override
	public Map toNetProtectElement(){
		Map m = super.toNetProtectElement();
		m.put(ELEMENT_TYPE, "PATTERN");
		m.put(ELEMENT_VALUE, "true");
		return m;
	}

	
	@Override
	public void toNetProtectElement(List<Map> elements){
		elements.add(toNetProtectElement());
		for (Element e : _elements){
			e.toNetProtectElement(elements);
		}
	}
	
	@Override
	public String toString(){
		Bundle extras = getExtras();
		return  super.toString() + ((extras == null || extras.isEmpty()) ? "" : " " + extras);		
	}
}

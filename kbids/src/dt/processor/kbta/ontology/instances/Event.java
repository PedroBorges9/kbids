package dt.processor.kbta.ontology.instances;

import java.util.List;
import java.util.Map;

import android.os.Bundle;

public final class Event extends Element{

	public Event(String name, long start, long end, Bundle extras){
		super(EVENT, name, start, end, extras);
	}

	@Override
	public int compareTo(Element another) {
		return 0;
	}

	@Override
	protected Map toNetProtectElement(){
		Map m = super.toNetProtectElement();
		m.put(ELEMENT_NAME, _name + "_Event");
		m.put(ELEMENT_TYPE, "EVENT");
		m.put(ELEMENT_VALUE, 1);
		return m;
	}
	
	@Override
	public void toNetProtectElement(List<Map> elements){
		elements.add(toNetProtectElement());
	}
}

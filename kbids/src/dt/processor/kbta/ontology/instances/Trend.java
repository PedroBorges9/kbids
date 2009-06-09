/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Trend extends Abstraction{
	private final Primitive _first;

	private Primitive _last;

	public Trend(String name, String value, TimeInterval timeInterval, Bundle extras,
		Primitive first, Primitive last){
		super(TREND, name, value, timeInterval, extras);
		_first = first;
		_last = last;
	}

	public void setLast(Primitive last){
		_last = last;
	}
		
	public Primitive getLast(){
		return _last;
	}
	
	public Primitive getFirst(){
		return _first;
	}

	@Override
	public int compareTo(Element another) {
		return 0;
	}
	
	@Override
	protected Map toNetProtectElement(){
		Map m = super.toNetProtectElement();
		m.put(ELEMENT_TYPE, "GRADIENT");
		m.put(ELEMENT_VALUE, _value);
		return m;
	}
	
	@Override
	public void toNetProtectElement(List<Map> elements){
		elements.add(toNetProtectElement());
		//TODO Add abstract-from/context elements
	}
}

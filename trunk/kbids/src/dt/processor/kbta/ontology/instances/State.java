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
public final class State extends Abstraction{

	public State(String name, String value, TimeInterval timeInterval, Bundle extras){
		super(STATE, name, value, timeInterval, extras);
	}

	@Override
	public int compareTo(Element another) {
		if (another.getClass().equals(State.class)){
			String result=((State) another).getValue();
			if (result.equals(_value)){
				return 0;

			}
			else if (_value.equalsIgnoreCase("High")){
				return 1;
			}
			else if (result.equalsIgnoreCase("High")){
				return -1;
			}
			else if (result.equalsIgnoreCase("Low")){
				return 1;
			}
			else{
				return -1;
			}	
		}
		return 0;
	}
	
	@Override
	protected Map toNetProtectElement(){
		Map m = super.toNetProtectElement();
		m.put(ELEMENT_TYPE, "STATE");
		m.put(ELEMENT_VALUE, _value);
		return m;
	}
	
	@Override
	public void toNetProtectElement(List<Map> elements){
		elements.add(toNetProtectElement());
		//TODO Add abstract-from/context elements
	}
}

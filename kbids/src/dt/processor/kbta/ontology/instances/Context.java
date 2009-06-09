/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Context extends Element{
	public Context(String name, TimeInterval timeInterval, Bundle extras){
		super(CONTEXT, name, timeInterval, extras);
	}

	@Override
	public int compareTo(Element another) {
		return 0;
	}	
	
	@Override
	protected Map toNetProtectElement(){
		Map m = super.toNetProtectElement();
		m.put(ELEMENT_TYPE, "CONTEXT");
		m.put(ELEMENT_VALUE, "DEFAULT_SYMBOLIC_VALUE");
		return m;
	}
	
	@Override
	public void toNetProtectElement(List<Map> elements){
		elements.add(toNetProtectElement());
	}
}

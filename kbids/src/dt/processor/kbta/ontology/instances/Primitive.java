/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.Date;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author 
 *
 */
public class Primitive extends Element {
	private final double _value;
	
	public Primitive( String name, double value, Date start, Date end) {
		 super(Element.PRIMITIVE, name, new TimeInterval(start.getTime(), end.getTime()));
		_value=value;
	}
	
	
	public double getValue(){
		return _value;
	}


	public String toString(){	
		return "Primitive "+" name= "+_name+" time interval "+_timeInterval+" value= "+_value;
	}

	
	

}

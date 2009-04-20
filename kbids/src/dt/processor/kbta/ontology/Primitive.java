/**
 * 
 */
package dt.processor.kbta.ontology;

import java.util.Date;

/**
 * @author 
 *
 */
public class Primitive extends Element {
	private final double _value;
	
	public Primitive(ElementDef elementDef, String name, double value, Date start, Date end) {
		 super(elementDef, name, start.getTime(), end.getTime());
		_value=value;
	}
	
	public String toString(){	
		return "Primitive "+" name= "+_name+" start= "+_start+" end= "+_end+" value= "+_value;
	}

	
	

}

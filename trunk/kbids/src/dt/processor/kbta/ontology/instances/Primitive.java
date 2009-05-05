/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.Date;

/**
 * @author
 */
public class Primitive extends Element{
	private final double _value;

	public Primitive(String name, double value, Date start, Date end){
		super(PRIMITIVE, name, start.getTime(), end.getTime());
		_value = value;
	}

	public double getValue(){
		return _value;
	}

	public String toString(){
		return "Primitive " + super.toString() + " value= " + _value;
	}

}

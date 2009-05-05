/**
 * 
 */
package dt.processor.kbta.ontology.instances;

/**
 * @author
 */
public final class Primitive extends Element{
	private final double _value;

	public Primitive(String name, double value, long start, long end){
		super(PRIMITIVE, name, start, end);
		_value = value;
	}

	public double getValue(){
		return _value;
	}

	@Override
	public String toString(){
		return super.toString() + " value= " + _value;
	}

}

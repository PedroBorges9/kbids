/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.Date;

import dt.processor.kbta.ontology.instances.Primitive;

/**
 * @author
 */
public class PrimitiveDef extends ElementDef{
	private final NumericRange _range;

	public PrimitiveDef(String name, NumericRange range){
		super(name);
		_range = range;
	}

	public Primitive definePrimitive(Date start, Date end, double value){
		Primitive p = null;
		if (_range.inRange(value)){
			p = new Primitive(_name, value, start, end);
		}
		return p;
	}

	@Override
	public String toString(){
		return "<Primitive name= " + _name + " " + _range + " />";
	}

}

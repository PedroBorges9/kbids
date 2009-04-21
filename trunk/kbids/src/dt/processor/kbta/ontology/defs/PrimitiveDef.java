/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.Date;

import dt.processor.kbta.ontology.instances.Primitive;

/**
 * @author
 * 
 */
public class PrimitiveDef extends ElementDef {
	private final double _min;
	private final double _max;
	private final boolean _isMinE;
	private final boolean _isMaxE;

	public PrimitiveDef(String name, double minValue, boolean minEquals,
			double maxValue, boolean maxEquals) {
		super(name);
		_min = minValue;
		_isMinE = minEquals;
		_max = maxValue;
		_isMaxE = maxEquals;
	}

	public Primitive definePrimitive(Date start, Date end,
			double value) {
		Primitive p = null;

		boolean ok = ((_isMinE) ? value >= _min : value > _min);
		ok = ok && ((_isMaxE) ? value <= _max : value < _max);

		if (ok) {
			p = new Primitive(this,_name, value, start, end);
		}
		return p;
	}

	@Override
	public String toString() {
		return "<Primitive name= " + _name + " min= " + _min + " isMinE= "
				+ _isMinE + " max=" + _max + " isMaxE= " + _isMaxE + " />";
	}

}

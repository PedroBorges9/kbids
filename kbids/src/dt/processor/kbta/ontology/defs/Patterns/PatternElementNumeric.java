package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.threats.DurationCondition;

public class PatternElementNumeric extends PatternElement {
	private NumericRange _numricRange;

	public PatternElementNumeric(int type,String name, int ordinal,
			DurationCondition duration, NumericRange numricRange) {
		super(type,name, ordinal, duration);
		_numricRange = numricRange;
	}
	
	@Override
	public String toString(){
		return super.toString()+"  "+_numricRange;
	}
	@Override
	protected boolean obeys(Element e) {
		// TODO Auto-generated method stub
		return (super.obeys(e) && _numricRange.isInRange(((Primitive) e).getValue()));
	}

}

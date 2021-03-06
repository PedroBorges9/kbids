package dt.processor.kbta.ontology.defs.patterns.patternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.PrimitiveContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.threats.DurationCondition;

public final class PatternElementPrimitive extends PatternElement{
	private NumericRange _numricRange;

	public PatternElementPrimitive(int type, String name, int ordinal,
		DurationCondition duration, NumericRange numricRange){
		super(type, name, ordinal, duration);
		_numricRange = numricRange;
	}

	@Override
	public ArrayList<Element> getValidElements(AllInstanceContainer aic){
		ArrayList<Element> ans = new ArrayList<Element>();
		PrimitiveContainer pc = aic.getPrimitives();
		Element e;
		e = pc.getOldPrimitive(_name);
		if (e != null && check(e)){
			ans.add(e);
		}
		e = pc.getCurrentPrimitive(_name);
		if (e != null && check(e)){
			ans.add(e);
		}
		if (ans.isEmpty()){
			return null;
		}
		return ans;
	}

	@Override
	public String toString(){
		return super.toString() + "  " + _numricRange;
	}

	@Override
	protected boolean check(Element e){
		return (super.check(e) && _numricRange.isInRange(((Primitive)e).getValue()));
	}

	@Override
	public ElementDef getElementDef(Ontology ontology){

		return ontology.getPrimitiveDef(_name);
	}
}

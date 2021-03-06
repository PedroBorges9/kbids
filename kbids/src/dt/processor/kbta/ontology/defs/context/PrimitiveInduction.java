package dt.processor.kbta.ontology.defs.context;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Primitive;

public class PrimitiveInduction extends Induction{
	private final NumericRange _numericValues;

	public PrimitiveInduction(String elementName, String contextName, NumericRange numericValues){
		super(elementName, contextName);
		_numericValues = numericValues;
	}

	@Override
	public boolean induce(AllInstanceContainer container){
		Primitive p = container.getPrimitives().getCurrentPrimitive(_elementName);
		if (p != null){
			if (_numericValues.isInRange(p.getValue())){
				// We use the fact that for primitives the start and end
				// time is the same and avoid checking _relativeToStart
				long start = p.getTimeInterval().getEndTime();
				long end = getEndTime(start);
				return createContext(container, start, end, p.getExtras(), p);
			}
		}
		return false;
	}
	
	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getPrimitiveDef(_elementName);
	}

	@Override
	public String getElementDefDescription(){
		return " type=Primitive " + "name=" + _elementName;
	}
}

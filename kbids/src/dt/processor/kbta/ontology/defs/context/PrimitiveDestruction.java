package dt.processor.kbta.ontology.defs.context;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.ontology.instances.Primitive;


public class PrimitiveDestruction extends Destruction {
	private final NumericRange _numericValues;
	
	public PrimitiveDestruction(String elementName, String contextName, NumericRange range){
		super(elementName, contextName);
		_numericValues=range;
	}

	

	@Override
	public boolean Destruct(AllInstanceContainer container) {
		Primitive primitive = container.getPrimitives().getCurrentPrimitive(_elementName);
		if (primitive != null && _numericValues.isInRange(primitive.getValue())){
			ComplexContainer<Context> cc=container.getContexts();
			Context c=cc.getCurrentElement(_contextName);
			if (c!=null){
				c.getTimeInterval().setEndTime(primitive.getTimeInterval().getEndTime());
				cc.removeCurrentElement(_contextName);
				cc.addToOld(c, _contextName);
				return true;
			}
		}
		return false;
	}

}

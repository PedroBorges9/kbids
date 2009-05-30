package dt.processor.kbta.ontology.defs.context;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;


public class StateDestruction extends Destruction {
	private final String _symbolicValue;
	
	public StateDestruction(String elementName, String contextName, String value){
		super(elementName, contextName);
		_symbolicValue=value;
	}

	

	@Override
	public boolean destroy(AllInstanceContainer container) {
		State state = container.getStates().getNewestElement(_elementName);
		if (state != null && _symbolicValue.equals(state.getValue())){
			ComplexContainer<Context> cc=container.getContexts();
			Context c=cc.getCurrentElement(_contextName);
			if (c!=null){
				c.getTimeInterval().setEndTime(state.getTimeInterval().getEndTime());
				cc.removeCurrentElement(_contextName);
				cc.addToOld(c, _contextName);
				return true;
			}
		}
		return false;
	}
}

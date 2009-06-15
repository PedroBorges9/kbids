package dt.processor.kbta.ontology.defs.context;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.util.TimeInterval;

public class StateInduction extends Induction{
	private final String _symbolicValue;

	public StateInduction(String elementName, String contextName, String symbolicValue){
		super(elementName, contextName);
		_symbolicValue = symbolicValue;
	}

	@Override
	public boolean induce(AllInstanceContainer container){
		State s = container.getStates().getNewestElement(_elementName);
		if (s != null){
			if (_symbolicValue.equalsIgnoreCase(s.getValue())){
				TimeInterval ti = s.getTimeInterval();
				long start = ti.getStartTime();
				long end = getEndTime(_relativeToStart ? start : ti.getEndTime());
				return createContext(container, start, end, s.getExtras(), s);
			}
		}
		return false;
	}
	
	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getStateDef(_elementName);
	}
	@Override
	public String getElementDefDescription(){
		return " type=State " + "name=" + _elementName;
	}

}

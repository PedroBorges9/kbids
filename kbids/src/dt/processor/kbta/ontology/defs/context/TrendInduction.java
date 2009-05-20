package dt.processor.kbta.ontology.defs.context;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.util.TimeInterval;

public class TrendInduction extends Induction{
	private final String _symbolicValue;

	public TrendInduction(String elementName, String contextName, String symbolicValue){
		super(elementName, contextName);
		_symbolicValue = symbolicValue;
	}

	@Override
	public boolean induce(AllInstanceContainer container){
		Trend t = container.getTrends().getNewestElement(_elementName);
		if (t != null){
			if (_symbolicValue.equalsIgnoreCase(t.getValue())){
				TimeInterval ti = t.getTimeInterval();
				long start = ti.getStartTime();
				long end = (_relativeToStart ? start : ti.getEndTime()) + _gap;
				return createContext(container, start, end, t.getExtras());
			}
		}
		return false;
	}
	
	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getTrendDef(_elementName);
	}

}

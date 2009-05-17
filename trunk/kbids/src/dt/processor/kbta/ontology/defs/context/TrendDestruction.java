package dt.processor.kbta.ontology.defs.context;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.ontology.instances.Trend;


public class TrendDestruction extends Destruction {
private final String _symbolicValue;
	
	public TrendDestruction(String elementName, String contextName, String value){
		super(elementName, contextName);
		_symbolicValue=value;
	}

	

	@Override
	public boolean Destruct(AllInstanceContainer container) {
		Trend trend = container.getTrends().getNewestElement(_elementName);
		if (trend != null && _symbolicValue.equals(trend.getValue())){
			ComplexContainer<Context> cc=container.getContexts();
			Context c=cc.getCurrentElement(_contextName);
			if (c!=null){
				c.getTimeInterval().setEndTime(trend.getTimeInterval().getEndTime());
				cc.removeCurrentElement(_contextName);
				cc.addToOld(c, _contextName);
				return true;
			}
		}
		return false;
	}
}

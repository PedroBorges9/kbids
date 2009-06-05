package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Abstraction;
import dt.processor.kbta.ontology.instances.Trend;

public class GeneratedFromTrend extends GeneratedFromAbstraction{

	public GeneratedFromTrend(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);

	}


	@Override
	public String toString(){
		return "Trend: " + super.toString();
	}

	@Override
	public String getElementDefDescription(){
		return " type=Trend " + "name=" + _elementName;
	}

	@Override
	protected Abstraction getCurrentAbstraction(AllInstanceContainer allInstances, String name){
		ComplexContainer<Trend> trends = allInstances.getTrends();
		return trends.getCurrentElement(_elementName);
	}
	
	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getTrendDef(_elementName);
	}
	
}

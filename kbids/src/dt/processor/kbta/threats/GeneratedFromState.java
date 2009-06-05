package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Abstraction;
import dt.processor.kbta.ontology.instances.State;

public class GeneratedFromState extends GeneratedFromAbstraction{

	public GeneratedFromState(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);

	}

	@Override
	public String toString(){
		return "State: " + super.toString();
	}

	@Override
	public String getElementDefDescription(){
		return " type=State " + "name=" + _elementName;
	}

	@Override
	protected Abstraction getCurrentAbstraction(AllInstanceContainer allInstances, String name){
		ComplexContainer<State> states = allInstances.getStates();
		return states.getCurrentElement(_elementName);
	}

	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getStateDef(_elementName);
	}
}

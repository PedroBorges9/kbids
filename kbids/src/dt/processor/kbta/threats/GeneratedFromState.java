package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.State;

public class GeneratedFromState extends GeneratedFrom{

	public GeneratedFromState(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);

	}

	@Override
	public Element locateMatchingElement(AllInstanceContainer allInstances){
		ComplexContainer<State> states = allInstances.getStates();
		State state = states.getCurrentElement(_elementName);
		if (state == null){
			return null;
		}

		String value = state.getValue();
		long dur = state.getTimeInterval().getDuration();
		if ((_symbolicValueCondition == null ? true : _symbolicValueCondition
				.check(value))
			&& _durationCondition.check(dur)){
			return state;
		}else{
			return null;
		}
	}

	@Override
	public String toString(){
		return "State: " + super.toString();
	}
}

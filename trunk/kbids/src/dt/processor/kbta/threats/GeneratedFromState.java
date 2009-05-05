package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.instances.State;

public class GeneratedFromState extends GeneratedFrom{

	public GeneratedFromState(String name, SymbolicValueCondition symbolicValueCondition,
		DurationCondition durationCondition){
		super(name, symbolicValueCondition, durationCondition);

	}

	@Override
	public boolean matchConditions(AllInstanceContainer allInstances){
		ComplexContainer<State> states = allInstances.getStates();
		State state = states.getCurrentElement(_elementName);
		if (state == null){
			return false;
		}

		String value = state.getValue();
		long dur = state.getTimeInterval().getDuration();

		return (_symbolicValueCondition == null ? true : _symbolicValueCondition
				.check(value))
				&& _durationCondition.check(dur);

	}

	@Override
	public String toString(){
		return "State: " + super.toString();
	}
}

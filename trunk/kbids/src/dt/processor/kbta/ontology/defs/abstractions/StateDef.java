/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;
import java.util.Arrays;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.instances.*;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class StateDef extends AbstractionDef{

	public StateDef(String name, ArrayList<AbstractedFrom> abstractedFrom,
		ArrayList<String> necessaryContexts, MappingFunction mappingFunction,
		InterpolationFunction interpolationFunction){
		super(name, abstractedFrom, necessaryContexts, mappingFunction,
				interpolationFunction);
	}

	public void createState(AllInstanceContainer instances, int iteration){
		// Making sure that an instance of this abstraction wasn't already created
		// in this iteration
		if (!assertNotCreatedIn(iteration)){
			return;
		}

		// Making sure all of the elements we need for the abstraction
		// are present
		// System.out.println("Creating state: " + _name);
		Element[] elementsAf = checkAbstractedFrom(instances);
		if (elementsAf == null){
			return;
		}
		// System.out.println("AF elements: " + Arrays.toString(elementsAf));
		// Making sure all of the contexts we need for the abstraction
		// are present
		Element[] elementsContext = checkNecessaryContexts(instances);
		if (elementsContext == null){
			return;
		}
		// System.out.println("Context elements: " + Arrays.toString(elementsContext));

		// Intersecting the elements to obtain the initial time interval
		// for the state
		TimeInterval timeInterval = TimeInterval.intersection(elementsAf, null);
		if (timeInterval == null){
			return;
		}
		// System.out.println("Element intersection: " + timeInterval);
		// Further intersecting the obtained time interval to obtain
		// the final time interval for the state
		timeInterval = TimeInterval.intersection(elementsContext, timeInterval);
		if (timeInterval == null){
			return;
		}
		// System.out.println("Context and Element intersection: " + timeInterval);
		// Mapping the elements to a state value, if possible
		String value = _mappingFunction.mapElements(elementsAf);
		if (value == null){
			return;
		}
		// System.out.println("Mapped value is: " + value);
		// From this point on, we are certain that a state can be created

		// Attempting to interpolate the newly created state with
		// an older state (which can only reside in the current elements)
		ComplexContainer<State> states = instances.getStates();
		State state = states.getCurrentElement(_name);

		// System.out.println("Previous state: " + state);
		if (state != null
				&& _interpolationFunction.interpolate(state, value, timeInterval)){
			// The interpolation has succeeded (and so the state's interval has already
			// been internally modified) and so we only need to remove it from
			// the current elements
			// System.out.println("Interpolation succeeded");
			states.removeCurrentElement(_name);
		}else{
			// Either there is no previous state to interpolate with
			// or the interpolation has failed, in either case we need
			// to create a new state
			state = new State(_name, value, timeInterval);
		}
		// Setting the newly created / interpolated state as the newest state of it's name
		states.setNewestElement(state);
		// Marking that the state of this name has already been created during this
		// iteration
		setLastCreated(iteration);
	}

	private Element[] checkNecessaryContexts(AllInstanceContainer instances){
		Element[] elements = new Element[_necessaryContexts.length];
		int i = 0;
		for (String st : _necessaryContexts){
			Context context = instances.getContexts().getNewestElement(st);
			if (context != null){
				elements[i++] = context;
			}else if ((context = instances.getContexts().getCurrentElement(st)) != null){
				elements[i++] = context;
			}else{
				return null;
			}
		}
		return elements;
	}

	private Element[] checkAbstractedFrom(AllInstanceContainer instances){
		Element[] elements = new Element[_abstractedFrom.length];
		int i = 0;
		Element element = null;
		for (AbstractedFrom af : _abstractedFrom){
			switch (af.getType()){
				case Element.PRIMITIVE:
					element = instances.getPrimitives().getCurrentPrimitive(af.getName());
					break;
				case Element.STATE:
					element = instances.getStates().getCurrentElement(af.getName());
					break;
				case Element.TREND:
					element = null; // TODO Implement trend lookup
					break;
			}
			if (element == null){
				return null;
			}
			elements[i++] = element;

		}
		return elements;
	}

	@Override
	public String toString(){
		String st = "name=" + _name + "\n";
		st += "AbstractedFrom\n" + Arrays.toString(_abstractedFrom) + "\n";
		st += "NecessaryContexts\n" + Arrays.toString(_necessaryContexts) + "\n";
		st += _mappingFunction;
		st += _interpolationFunction;

		return st;
	}

}

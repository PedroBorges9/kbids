/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;
import java.util.Arrays;

import dt.processor.kbta.container.*;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public class StateDef extends AbstractionDef{
	private final AbstractedFrom[] _abstractedFrom;

	private final String[] _necessaryContexts;

	private final MappingFunction _mappingFunction;

	private final InterpolationFunction _interpolationFunction;

	public StateDef(String name, ArrayList<AbstractedFrom> abstractedFrom,
		ArrayList<String> necessaryContexts, MappingFunction mappingFunction,
		InterpolationFunction interpolationFunction){
		super(name);
		_abstractedFrom = abstractedFrom
				.toArray(new AbstractedFrom[abstractedFrom.size()]);
		_necessaryContexts = necessaryContexts.toArray(new String[necessaryContexts
				.size()]);
		_mappingFunction = mappingFunction;
		_interpolationFunction = interpolationFunction;

	}

	public void createState(AllInstanceContainer instances, int iteration){
		// Making sure all of the elements we need for the abstraction
		// are present
	//	System.out.println("Creating state: " + _name);
		Element[] elementsAf = checkAbstractedFrom(instances);
		if (elementsAf == null){
			return;
		}
	//	System.out.println("AF elements: " + Arrays.toString(elementsAf));
		// Making sure all of the contexts we need for the abstraction
		// are present
		Element[] elementsContext = checkNecessaryContexts(instances);
		if (elementsContext == null){
			return;
		}
	//	System.out.println("Context elements: " + Arrays.toString(elementsContext));

		// Intersecting the elements to obtain the initial time interval
		// for the state
		TimeInterval timeInterval = intersection(elementsAf, null);
		if (timeInterval == null){
			return;
		}
	//	System.out.println("Element intersection: " + timeInterval);
		// Further intersecting the obtained time interval to obtain
		// the final time interval for the state
		timeInterval = intersection(elementsContext, timeInterval);
		if (timeInterval == null){
			return;
		}
	//	System.out.println("Context and Element intersection: " + timeInterval);
		// Mapping the elements to a state value, if possible
		String value = _mappingFunction.mapElements(elementsAf);
		if (value == null){
			return;
		}
	//	System.out.println("Mapped value is: " + value);
		// From this point on, we are certain that a state can be created

		// Attempting to interpolate the newly created state with
		// an older state (which can only reside in the current elements)
		ComplexContainer<State> states = instances.getStates();
		State state = states.getCurrentElement(_name);

//		System.out.println("Previous state: " + state);
		if (state != null
				&& _interpolationFunction.interpolate(state, value, timeInterval)){
			// The interpolation has succeeded (and so the state's interval has already
			// been internally modified) and so we only need to remove it from
			// the current elements
		//	System.out.println("Interpolation succeeded");
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

	private TimeInterval intersection(Element[] elements, TimeInterval initialInterval){
		long endMin = (initialInterval == null) ? Long.MAX_VALUE : initialInterval
				.getEndTime();
		long startMax = (initialInterval == null) ? 0 : initialInterval.getStartTime();
		for (Element element : elements){
			TimeInterval timeInterval = element.getTimeInterval();
			long startTime = timeInterval.getStartTime();
			long endTime = timeInterval.getEndTime();
			startMax = (startTime > startMax) ? startTime : startMax;
			endMin = (endTime < endMin) ? endTime : endMin;
		}
		if (startMax <= endMin){
			return new TimeInterval(startMax, endMin);
		}
		return null;
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
		for (AbstractedFrom af : _abstractedFrom){
			switch (af.getType()){
				case Element.PRIMITIVE:
					Primitive primitive = instances.getPrimitives().getCurrentPrimitive(
						af.getName());
					if (primitive == null){
						return null;
					}
					elements[i++] = primitive;

					break;

				case Element.STATE:
					
				//	State state = instances.getStates().getNewestElement(af.getName());
				//	if (state == null){
					State state = instances.getStates().getCurrentElement(af.getName());
						if (state == null){
							return null;
						}
			//		}
					
					
					elements[i++] = state;
					break;

				case Element.TREND:
					return null;
					// break;

			}

		}
		return elements;
	}

	@Override
	public String toString(){
		String st = "name=" + _name + "\n";
		st += " AbstractedFrom\n" + Arrays.toString(_abstractedFrom) + "\n";
		st += " NecessaryContexts\n" + Arrays.toString(_necessaryContexts) + "\n";
		st += _mappingFunction;
		st += _interpolationFunction;

		return st;
	}

}

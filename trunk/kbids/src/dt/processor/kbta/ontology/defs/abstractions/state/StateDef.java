/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions.state;

import java.util.ArrayList;

import android.os.Bundle;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.abstractions.AbstractionDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class StateDef extends AbstractionDef{
	private final AbstractedFrom[] _abstractedFrom;

	private final StateMappingFunction _mappingFunction;

	private final InterpolationFunction _interpolationFunction;

	public StateDef(String name, ArrayList<AbstractedFrom> abstractedFrom,
		ArrayList<String> necessaryContexts, StateMappingFunction mappingFunction,
		InterpolationFunction interpolationFunction){
		super(name, necessaryContexts);
		_abstractedFrom = abstractedFrom
				.toArray(new AbstractedFrom[abstractedFrom.size()]);
		_mappingFunction = mappingFunction;
		_interpolationFunction = interpolationFunction;
	}

	public void createState(AllInstanceContainer instances, int iteration){
		// Making sure that an instance of this abstraction wasn't already created
		// in this iteration
		if (!assertNotCreatedIn(iteration)){
			return;
		}

		// Making sure all of the elements we need for the abstraction
		// are present
		Element[] elementsAf = checkAbstractedFrom(instances);
		if (elementsAf == null){
			return;
		}
		// Making sure all of the contexts we need for the abstraction
		// are present
		Element[] elementsContext = checkNecessaryContexts(instances);
		if (elementsContext == null){
			return;
		}

		// Intersecting the elements to obtain the initial time interval
		// for the abstraction
		TimeInterval timeInterval = TimeInterval.intersection(elementsAf, null);
		if (timeInterval == null){
			return;
		}
		
		// Further intersecting the obtained time interval to obtain
		// the final time interval for the abstraction
		timeInterval = TimeInterval.intersection(elementsContext, timeInterval);
		if (timeInterval == null){
			return;
		}
		
		// Mapping the elements to a state value, if possible
		String value = _mappingFunction.mapElements(elementsAf);
		if (value == null){
			return;
		}

		// From this point on, we are certain that an abstraction can be created
		// so we can sum up the extras of the abstracted-from elements and the
		// contexts
		Bundle newExtras = new Bundle();
		for (Element element : elementsAf){
			element.addInnerExtras(newExtras);
		}
		for (Element element : elementsContext){
			element.addInnerExtras(newExtras);
		}

		// Attempting to interpolate the newly created state with
		// an older state (which can only reside in the current elements)
		ComplexContainer<State> states = instances.getStates();
		State state = states.getCurrentElement(_name);

		if (state != null
				&& _interpolationFunction.interpolate(state, value, timeInterval)){
			// The interpolation has succeeded (and so the state's interval has already
			// been internally modified) and so we only need to remove it from
			// the current elements
			states.removeCurrentElement(_name);
			// Seeing as the abstraction has already existed, we only to its inner extras
			state.addToInnerExtras(newExtras);
		}else{
			// Either there is no previous state to interpolate with
			// or the interpolation has failed, in either case we need
			// to create a new state
			state = new State(_name, value, timeInterval, newExtras);
		}
		// Setting the newly created / interpolated state as the newest state of it's name
		states.setNewestElement(state);
		// Marking that the state of this name has already been created during this
		// iteration
		setLastCreated(iteration);
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
					element = instances.getTrends().getCurrentElement(af.getName());
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
		return "State: " + _name;
	}

	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		super.accept(ontology, visitor); // For necessary contexts and self

		for (AbstractedFrom af : _abstractedFrom){
			ElementDef elementDef=af.getElementDef(ontology);
			if (elementDef == null){
				throw new IllegalStateException("Undefined element:" + af);
			}
			elementDef.accept(ontology, visitor);
		}
	}
}

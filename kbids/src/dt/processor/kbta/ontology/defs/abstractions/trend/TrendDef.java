/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions.trend;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.container.PrimitiveContainer;
import dt.processor.kbta.ontology.defs.abstractions.AbstractionDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class TrendDef extends AbstractionDef{
	private final String _abstractedFrom;

	private final TrendMappingFunction _mappingFunction;

	public TrendDef(String name, String abstractedFrom,
		ArrayList<String> necessaryContexts, TrendMappingFunction mappingFunction){
		super(name, necessaryContexts);
		_abstractedFrom = abstractedFrom;
		_mappingFunction = mappingFunction;
	}

	public void createTrend(AllInstanceContainer instances, int iteration){
		// Making sure that an instance of this abstraction wasn't already created
		// in this iteration
		if (!assertNotCreatedIn(iteration)){
			return;
		}

		PrimitiveContainer primitives = instances.getPrimitives();
		Primitive primitive = primitives.getCurrentPrimitive(_abstractedFrom);
		if (primitive == null){
			return;
		}

		// Making sure all of the contexts we need for the abstraction
		// are present
		Element[] elementsContext = checkNecessaryContexts(instances);
		if (elementsContext == null){
			return;
		}

		// Intersecting the elements to obtain the time interval
		// for the abstraction
		TimeInterval timeInterval = TimeInterval.intersection(elementsContext, primitive
				.getTimeInterval());
		if (timeInterval == null){
			return;
		}

		ComplexContainer<Trend> trends = instances.getTrends();
		Trend currentTrend = trends.getCurrentElement(_name);
		// Mapping the elements to a state value, if possible

		// From this point on, we are certain that an abstraction can be created
		// so we can sum up the extras of the abstracted-from elements and the
		// contexts
		Bundle newExtras = new Bundle();
		primitive.addInnerExtras(newExtras);
		for (Element element : elementsContext){
			element.addInnerExtras(newExtras);
		}

		createTrend(iteration, primitives, primitive, trends, currentTrend, newExtras);
	}

	private void createTrend(int iteration, PrimitiveContainer primitives,
		Primitive primitive, ComplexContainer<Trend> trends, Trend currentTrend,
		Bundle newExtras){
		TimeInterval tiPrimitive = primitive.getTimeInterval();
		if (currentTrend != null){
			TimeInterval tiTrend = currentTrend.getTimeInterval();
			if (_mappingFunction.isGapSmallerThanMaxGap(tiTrend, tiPrimitive)){
				if (_mappingFunction.isInIterpolationRange(currentTrend, primitive)){
					currentTrend.setLast(primitive);
					tiTrend.setEndTime(tiPrimitive.getEndTime());
					currentTrend.setValue(_mappingFunction.mapValue(currentTrend
							.getFirst(), primitive));
					trends.removeCurrentElement(_name);
					// Seeing as the abstraction has already existed, we only to its inner extras
					currentTrend.addToInnerExtras(newExtras);
				}else{
					Primitive last = currentTrend.getLast();
					String value = _mappingFunction.mapValue(last, primitive);

					TimeInterval tiNew = new TimeInterval(tiTrend.getEndTime(),
							tiPrimitive.getEndTime());
					currentTrend = new Trend(_name, value, tiNew, newExtras, last,
							primitive);

				}
				trends.addElement(currentTrend);
				setLastCreated(iteration);
			}
		}else{
			Primitive old = primitives.getOldPrimitive(_name);
			if (old != null){
				String value = _mappingFunction.mapValue(old, primitive);
				TimeInterval tiNew = new TimeInterval(old.getTimeInterval().getEndTime(),
						tiPrimitive.getEndTime());

				currentTrend = new Trend(_name, value, tiNew, newExtras, old, primitive);

				trends.addElement(currentTrend);
				setLastCreated(iteration);
			}			
		}
	}

	@Override
	public String toString(){
		return "AbstractedFrom(primitive)= " + _abstractedFrom + "\n"
				+ "necessaryContexts " + Arrays.toString(_necessaryContexts) + "\n"
				+ "mappingFunction " + _mappingFunction;
	}

}

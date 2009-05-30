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
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.ElementDef.ElementVisitor;
import dt.processor.kbta.ontology.defs.abstractions.AbstractionDef;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractedFrom;
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

		// System.out.println("Creating trend: " + _name);
		// Making sure the element we need for the abstraction
		// are present
		PrimitiveContainer primitives = instances.getPrimitives();
		Primitive primitive = primitives.getCurrentPrimitive(_abstractedFrom);
		if (primitive == null){
			return;
		}
		// System.out.println("AF element (primitive): " + primitive);
		// Making sure all of the contexts we need for the abstraction
		// are present
		Element[] elementsContext = checkNecessaryContexts(instances);
		if (elementsContext == null){
			return;
		}

		// System.out.println("Context elements: " + Arrays.toString(elementsContext));
		// Intersecting the elements to obtain the time interval
		// for the abstraction
		TimeInterval timeInterval = TimeInterval.intersection(elementsContext, primitive
				.getTimeInterval());
		if (timeInterval == null){
			return;
		}

		// System.out.println("Element intersection: " + timeInterval);

		ComplexContainer<Trend> trends = instances.getTrends();
		Trend currentTrend = trends.getCurrentElement(_name);
		// Mapping the elements to a trend value, if possible
		// From this point on, we are certain that an abstraction can be created
		// so we can sum up the extras of the abstracted-from elements and the
		// contexts
		Bundle newExtras = new Bundle();
		primitive.addInnerExtras(newExtras);
		for (Element element : elementsContext){
			element.addInnerExtras(newExtras);
		}

		// create trend
		createTrend(iteration, primitives, primitive, trends, currentTrend, newExtras);
	}

	private void createTrend(int iteration, PrimitiveContainer primitives,
		Primitive primitive, ComplexContainer<Trend> trends, Trend currentTrend,
		Bundle newExtras){
		TimeInterval tiPrimitive = primitive.getTimeInterval();

		// System.out.println("currentTrend "+currentTrend);
		if (currentTrend != null){// exist current trend
			TimeInterval tiTrend = currentTrend.getTimeInterval();
			if (_mappingFunction.isGapSmallerThanMaxGap(tiTrend, tiPrimitive)){
				if (_mappingFunction.isInIterpolationRange(currentTrend, primitive)){
					// current primitive is in interpolate range then need to interpolate
					// between current primitive and current trend

					// System.out.println("InIterpolationRange");
					currentTrend.setLast(primitive);
					tiTrend.setEndTime(tiPrimitive.getEndTime());

					// System.out.println("value(threshold)
					// "+_mappingFunction.mapValue(currentTrend
					// .getFirst(), primitive));

					currentTrend.setValue(_mappingFunction.mapValue(currentTrend
							.getFirst(), primitive));

					// The interpolation has succeeded (and so the trend's interval has
					// already
					// been internally modified) and so we only need to remove it from
					// the current elements
					trends.removeCurrentElement(_name);
					// Seeing as the abstraction has already existed, we only to its inner
					// extras
					currentTrend.addToInnerExtras(newExtras);
				}else{
					// current primitive isn't in interpolate range then need to create
					// new trend by interpolate between the primitive in the end of the
					// current trend and the current primitive

					Primitive last = currentTrend.getLast();
					String value = _mappingFunction.mapValue(last, primitive);
					TimeInterval tiNew = new TimeInterval(tiTrend.getEndTime(),
							tiPrimitive.getEndTime());
					currentTrend = new Trend(_name, value, tiNew, newExtras, last,
							primitive);

				}
				// Setting the newly created / interpolated trend as the newest trend of
				// it's name
				trends.addElement(currentTrend);
				// Marking that the trend of this name has already been created during
				// this
				// iteration
				setLastCreated(iteration);
			}
		}else{
			// not exist current trend, then check if exist old primitive, if exist
			// then create new trend by interpolate between old primitive and current
			// primitive

			// checked
			Primitive old = primitives.getOldPrimitive(primitive.getName());
			// System.out.println("oldPrimitive "+old);
			if (old != null){
				String value = _mappingFunction.mapValue(old, primitive);
				TimeInterval tiNew = new TimeInterval(old.getTimeInterval().getEndTime(),
						tiPrimitive.getEndTime());

				currentTrend = new Trend(_name, value, tiNew, newExtras, old, primitive);

				// Setting the newly created trend as the newest trend of it's name
				trends.addElement(currentTrend);
				// Marking that the trend of this name has already been created during
				// this
				// iteration
				setLastCreated(iteration);
			}
		}
	}

	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		super.accept(ontology, visitor); // For necessary contexts and self

		ElementDef elementDef = ontology.getPrimitiveDef(_abstractedFrom);
		elementDef.accept(ontology, visitor);
	}

	@Override
	public String toString(){
		return "<Trend AbstractedFrom(primitive)= " + _abstractedFrom + "\n"
				+ "necessaryContexts " + Arrays.toString(_necessaryContexts) + "\n"
				+ "mappingFunction " + _mappingFunction + " isMonitored=" + _isMonitored
				+ " counter=" + _monitoredCounter + "/>";
	}
}

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

		// Making sure the element we need for the abstraction
		// are present
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

		// We can sum up the extras of the abstracted-from elements and the
		// contexts as they'll be the extras of the new trend
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

		if (currentTrend == null){
			// There isn't a current trend, so we check if an old primitive exists, if so
			// we create a new trend between the old primitive and current primitive
			Primitive old = primitives.getOldPrimitive(primitive.getName());
			if (old != null){
				String value = _mappingFunction.mapValue(old, primitive);
				TimeInterval tiNew = new TimeInterval(old.getTimeInterval().getEndTime(),
						tiPrimitive.getEndTime());

				currentTrend = new Trend(_name, value, tiNew, newExtras, old, primitive);

				addCreatedTrend(iteration, trends, currentTrend);
			}
		}else{
			// A current trend exists so we check if we can add the new primitive to it
			TimeInterval tiTrend = currentTrend.getTimeInterval();
			if (_mappingFunction.isGapSmallerThanMaxGap(tiTrend, tiPrimitive)){
				if (_mappingFunction.isInIterpolationRange(currentTrend, primitive)){
					// The current primitive is in interpolate range so we update
					// the current trend according the the current primitive
					currentTrend.setLast(primitive);
					tiTrend.setEndTime(tiPrimitive.getEndTime());

					currentTrend.setValue(_mappingFunction.mapValue(currentTrend
							.getFirst(), primitive));

					// The interpolation has succeeded and so we only need to remove the
					// trend from the current elements
					trends.removeCurrentElement(_name);
					// Seeing as the abstraction has already existed, we only to its inner
					// extras
					currentTrend.addToInnerExtras(newExtras);
				}else{
					// The current primitive isn't in interpolation range so we need to
					// create a new trend between the current primitive and the primitive
					// in the end (namely last) of the current trend
					Primitive last = currentTrend.getLast();
					String value = _mappingFunction.mapValue(last, primitive);
					TimeInterval tiNew = new TimeInterval(tiTrend.getEndTime(),
							tiPrimitive.getEndTime());
					currentTrend = new Trend(_name, value, tiNew, newExtras, last,
							primitive);

				}
				addCreatedTrend(iteration, trends, currentTrend);
			}
		}
	}

	private void addCreatedTrend(int iteration, ComplexContainer<Trend> trends,
		Trend currentTrend){
		// Setting the newly created trend as the newest trend of it's name
		trends.addElement(currentTrend);
		// Marking that the trend of this name has already been created during
		// this iteration
		setLastCreated(iteration);
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

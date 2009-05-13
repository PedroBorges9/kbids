/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions.trend;

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

	private final InterpolateMappingFunction _mappingFunction;

	public TrendDef(String name, String abstractedFrom,
		ArrayList<String> necessaryContexts, InterpolateMappingFunction mappingFunction){
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
		Element primitive = primitives.getCurrentPrimitive(_abstractedFrom);
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

		Bundle newExtras = new Bundle();

		if (currentTrend != null
				&& _mappingFunction.mapElements(currentTrend, (Primitive)primitive)){
			trends.removeCurrentElement(_name);
		}else{
			if (currentTrend == null){
				// TODO try to interpolate between two primitives(current and old) by
				// check
				// treshold
				Primitive old = primitives.getOldPrimitive(_name);
				if (old == null){
					return;
				}else{
					currentTrend = new Trend(_name, _mappingFunction.checkTreshold(old,
						(Primitive)primitive), new TimeInterval(old.getTimeInterval()
							.getEndTime(), primitive.getTimeInterval().getEndTime()),
							newExtras, old, (Primitive)primitive);
					trends.addElement(currentTrend);

				}

			}else{// mapping failed
				if (primitive.getTimeInterval().getEndTime()
						- currentTrend.getTimeInterval().getEndTime() > _mappingFunction
						.getMaxGap()){
					// need to remove currentTrend ???
					trends.removeCurrentElement(_name);
				}else{// check angles failed
					// create new Trend with primitive and last in trend and enter to
					// _trends
					Primitive last = currentTrend.getLast();
					currentTrend = new Trend(_name, _mappingFunction.checkTreshold(last,
						(Primitive)primitive), new TimeInterval(last.getTimeInterval()
							.getEndTime(), primitive.getTimeInterval().getEndTime()),
							newExtras, last, (Primitive)primitive);
					trends.addElement(currentTrend);

				}

			}

		}

		setLastCreated(iteration);

	}

	@Override
	public String toString(){
		return "AbstractedFrom(primitive)= " + _abstractedFrom + "\n"
				+ "necessaryContexts " + Arrays.toString(_necessaryContexts) + "\n"
				+ "mappingFunction " + _mappingFunction;
	}

}

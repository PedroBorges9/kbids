package dt.processor.kbta.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.util.Log;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.Patterns.LinearPatternDef;
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;

public final class Ontology{
	private final HashMap<String, PrimitiveDef> _primitives;

	private final HashMap<String, EventDef> _events;

	private final ContextDef[] _contexts;

	private final StateDef[] _states;

	private final TrendDef[] _trends;
	
	private final LinearPatternDef[] _linearPatterns;
	
	private final long _elementTimeout;

	private final String _ontologyName;

	private final String _version;
		
	Ontology(HashMap<String, PrimitiveDef> primitives,
		HashMap<String, EventDef> events, ArrayList<ContextDef> contexts,
		ArrayList<StateDef> states,ArrayList<TrendDef> trends, 
		ArrayList<LinearPatternDef> linearPatterns,	long elementTimeout, String ontologyName, String version){
		_primitives = primitives;
		_events = events;
		_version = version;
		_contexts = contexts.toArray(new ContextDef[contexts.size()]);
		_states = states.toArray(new StateDef[states.size()]);
		_trends = trends.toArray(new TrendDef[trends.size()]);
		
		_linearPatterns = linearPatterns.toArray(new LinearPatternDef[linearPatterns.size()]);
		Log.d("ONTOLOGY", Arrays.toString(_linearPatterns));
		_elementTimeout = elementTimeout;
		_ontologyName = ontologyName;
	}

	public ContextDef[] getContextDefiners(){
		return _contexts;
	}

	public StateDef[] getStateDefiners(){
		return _states;
	}

	public TrendDef[] getTrendDefiners(){
		return _trends;
	}
	
	public PrimitiveDef getPrimitiveDef(String name){
		return _primitives.get(name);
	}

	public EventDef getEventDef(String name){
		return _events.get(name);
	}
	
	public long getElementTimeout(){
		return _elementTimeout;
	}
	
	public String getName(){
		return _ontologyName;
	}
		
	public String getVersion(){
		return _version;
	}


	public LinearPatternDef[] getLinearPatternDefs() {
		return _linearPatterns;
	}

	
	public StateDef getStateDef(String name){
		for(StateDef sd :_states){
			if (sd.getName().equalsIgnoreCase(name)){
				return sd;
			}
		}
		return null;
	}
	
	public TrendDef getTrendDef(String name){
		for(TrendDef td :_trends){
			if (td.getName().equalsIgnoreCase(name)){
				return td;
			}
		}
		return null;
	}
	
	public ContextDef getContextDef(String name){
		for(ContextDef cd :_contexts){
			if (cd.getName().equalsIgnoreCase(name)){
				return cd;
			}
		}
		return null;
	}
	
	//TODO getPatternDef
	
	
	@Override
	public String toString(){
		String st="Ontology name="+_ontologyName;
		for(PrimitiveDef pd : _primitives.values()){
			st+=pd+"\n\n";
		}
		for(EventDef ed : _events.values()){
			st+=ed+"\n\n";
		}
		st+=Arrays.toString(_contexts)+"\n\n";
		st+=Arrays.toString(_states)+"\n\n";
		st+=Arrays.toString(_trends)+"\n\n";
		
		return st;
	}
	
	/**
	 * Resetting the monitored state of all of the elements in the ontology
	 */
	public void setAllElementsInitiallyUnmonitored(){
		for (ElementDef ed : _primitives.values()){
			ed.setInitiallyUnmonitored();
		}
		for (ElementDef ed : _events.values()){
			ed.setInitiallyUnmonitored();
		}
		for (ElementDef ed : _contexts){
			ed.setInitiallyUnmonitored();
		}
		for (ElementDef ed : _states){
			ed.setInitiallyUnmonitored();
		}
		for (ElementDef ed : _trends){
			ed.setInitiallyUnmonitored();
		}
		for (ElementDef ed : _linearPatterns){
			ed.setInitiallyUnmonitored();
		}
	}

}

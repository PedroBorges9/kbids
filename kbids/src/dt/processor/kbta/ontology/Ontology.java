package dt.processor.kbta.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.patterns.LinearPatternDef;
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
		
	public Ontology(HashMap<String, PrimitiveDef> primitives,
		HashMap<String, EventDef> events, ArrayList<ContextDef> contexts,
		ArrayList<StateDef> states,ArrayList<TrendDef> trends, 
		ArrayList<LinearPatternDef> linearPatterns,	long elementTimeout, String ontologyName, String version){
		_primitives = primitives;
		_events = events;
		_contexts = contexts.toArray(new ContextDef[contexts.size()]);
		_states = states.toArray(new StateDef[states.size()]);
		_trends = trends.toArray(new TrendDef[trends.size()]);		
		_linearPatterns = linearPatterns.toArray(new LinearPatternDef[linearPatterns.size()]);
		
		_elementTimeout = elementTimeout;
		_ontologyName = ontologyName;
		_version = version;
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
	
	public ContextDef[] getContextDefs(){
		return _contexts;
	}

	public StateDef[] getStateDefs(){
		return _states;
	}

	public TrendDef[] getTrendDefs(){
		return _trends;
	}

	public LinearPatternDef[] getLinearPatternDefs() {
		return _linearPatterns;
	}	

	public PrimitiveDef getPrimitiveDef(String name){
		return _primitives.get(name);
	}

	public EventDef getEventDef(String name){
		return _events.get(name);
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
	
	public ElementDef getLinearPatternDef(String name){
		for(LinearPatternDef lpd :_linearPatterns){
			if (lpd.getName().equalsIgnoreCase(name)){
				return lpd;
			}
		}
		return null;
	}
	
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
	 * Resetting all of the elements in the ontology
	 */
	public void resetOntology(){
		for (ElementDef ed : _primitives.values()){
			ed.resetElement();
		}
		for (ElementDef ed : _events.values()){
			ed.resetElement();
		}
		for (ElementDef ed : _contexts){
			ed.resetElement();
		}
		for (ElementDef ed : _states){
			ed.resetElement();
		}
		for (ElementDef ed : _trends){
			ed.resetElement();
		}
		for (ElementDef ed : _linearPatterns){
			ed.resetElement();
		}
	}
	
	/**
	 * Resetting the "last created iteation" property of all of the elements in the ontology
	 */
	public void resetLastCreated(){
		for (ElementDef ed : _primitives.values()){
			ed.resetLastCreated();
		}
		for (ElementDef ed : _events.values()){
			ed.resetLastCreated();
		}
		for (ElementDef ed : _contexts){
			ed.resetLastCreated();
		}
		for (ElementDef ed : _states){
			ed.resetLastCreated();
		}
		for (ElementDef ed : _trends){
			ed.resetLastCreated();
		}
		for (ElementDef ed : _linearPatterns){
			ed.resetLastCreated();
		}
	}
}

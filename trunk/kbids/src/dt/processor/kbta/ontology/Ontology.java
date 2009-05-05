package dt.processor.kbta.ontology;

import java.util.ArrayList;
import java.util.HashMap;

import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.abstractions.StateDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;

public class Ontology{
	private HashMap<String, PrimitiveDef> _primitiveDefiners;

	private HashMap<String, EventDef> _eventDefiners;

	private ArrayList<ContextDef> _contextDefiners;

	private HashMap<String, StateDef> _stateDefiners;

	public Ontology(){
		_primitiveDefiners = new HashMap<String, PrimitiveDef>();
		_eventDefiners = new HashMap<String, EventDef>();
		_stateDefiners = new HashMap<String, StateDef>();
		_contextDefiners = new ArrayList<ContextDef>();
	}

	public ArrayList<ContextDef> getContextDefiners(){
		return _contextDefiners;
	}

	public HashMap<String, StateDef> getStateDefiners(){
		return _stateDefiners;
	}

	public void AddContextDefiners(ContextDef c){
		_contextDefiners.add(c);
	}

	public void addPrimitiveDef(PrimitiveDef p){
		_primitiveDefiners.put(p.getName(), p);
	}

	public void addEventDef(EventDef e){
		_eventDefiners.put(e.getName(), e);
	}

	public void addStateDef(StateDef s){
		_stateDefiners.put(s.getName(), s);
	}

	public PrimitiveDef getPrimitiveDef(String name){
		return _primitiveDefiners.get(name);
	}

	public EventDef getEventDef(String name){
		return _eventDefiners.get(name);
	}

	public StateDef getStateDef(String name){
		return _stateDefiners.get(name);
	}

	public String printStates(){

		String st = "";
		for (StateDef sd : _stateDefiners.values()){
			st += "*************************\n" + sd + "\n";
		}

		return st;
	}

}

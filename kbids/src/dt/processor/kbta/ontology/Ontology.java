package dt.processor.kbta.ontology;

import java.util.HashMap;

public class Ontology {
	private HashMap<String, PrimitiveDef> _primitiveDefiners; 
	private HashMap<String, EventDef> _eventDefiners;
	
	public Ontology(){
		_primitiveDefiners=new HashMap<String, PrimitiveDef>();
		_eventDefiners=new HashMap<String, EventDef>();
	}

	public void addPrimitiveDef(PrimitiveDef p){
		_primitiveDefiners.put(p.getName(),p);
	}
	public void addEventDef(EventDef e){
		_eventDefiners.put(e.getName(),e);
	}

	public PrimitiveDef getPrimitiveDef(String name){
		
		return _primitiveDefiners.get(name);
	}

	public EventDef getEventDef(String name) {
		return _eventDefiners.get(name);
	}
}

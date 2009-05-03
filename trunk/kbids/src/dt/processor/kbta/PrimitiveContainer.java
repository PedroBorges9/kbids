package dt.processor.kbta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.ontology.instances.Primitive;

public class PrimitiveContainer implements ElementContainer{
	private final HashMap<String, Primitive> _oldElements;
	private final HashMap<String, Primitive> _currentElements; 

	public PrimitiveContainer(){
		_oldElements = new HashMap<String, Primitive>();
		_currentElements = new HashMap<String, Primitive>();

		// TODO Auto-generated constructor stub
	}

	public void addPrimitive(Primitive p){
		String name = p.getName();
		_currentElements.put(name, p);
	}

	public Primitive getCurrentPrimitive(String st){
		return _currentElements.get(st);	 
	}

	public Primitive getOldPrimitive(String st){
		return _oldElements.get(st);	 
	}
	public void shiftBack(){
		for (Map.Entry<String, Primitive> newPrimitive: _currentElements.entrySet()){
			String name=newPrimitive.getKey();
			_oldElements.put(name, newPrimitive.getValue());
		}
		_currentElements.clear();
	}

	@Override
	public void discardOlderThen(long time){
		Iterator<Map.Entry<String, Primitive>> currentIter=_currentElements.entrySet().iterator();
		while(currentIter.hasNext()){
			Map.Entry<String, Primitive> currentPrimitive = currentIter.next();
			String name=currentPrimitive.getKey();
			if (currentPrimitive.getValue().getEnd()<time){
				_oldElements.remove(name);
				currentIter.remove();
			}
		}

		Iterator<Map.Entry<String, Primitive>> oldIter=_oldElements.entrySet().iterator();
		while(oldIter.hasNext()){
			Map.Entry<String, Primitive> oldPrimitive = oldIter.next();
			if (oldPrimitive.getValue().getEnd()<time){
				oldIter.remove();
			}
		}




	}

}

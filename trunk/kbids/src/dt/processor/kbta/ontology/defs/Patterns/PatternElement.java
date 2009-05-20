package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.container.ElementContainer;
import dt.processor.kbta.container.EventContainer;
import dt.processor.kbta.container.PrimitiveContainer;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.threats.DurationCondition;

public class PatternElement {
	private int _type;
	private String _name;
	private int _ordinal;
	private DurationCondition _duration;
	public PatternElement(int type,String name, int ordinal, DurationCondition duration) {
		_type = type;
		_ordinal = ordinal;
		_duration = duration;
		_name = name;
	}
	
	@Override
	public String toString(){
		return "type= "+_type+" name= "+_name+" ordinal= "+_ordinal+_duration;
	}

	public Integer getOrdinal() {
		return _ordinal;
	}

	public Element getValid(AllInstanceContainer aic) {
		
		Element e = getElement(aic);
		
		
		if (e!=null && obeys(e)){
			return e;
		}
			
		return null;
					
	}

	private Element getElement(AllInstanceContainer aic) {
		Element e=null;
		if (_type==Element.CONTEXT ||_type==Element.STATE ||_type==Element.TREND){
			ComplexContainer cc;
			if (_type==Element.CONTEXT){
				cc=aic.getContexts();
			}
			else if (_type==Element.STATE){
				cc=aic.getStates();
			}
			else{
				cc=aic.getTrends();
			}
			e=cc.getNewestElement(_name);
			if (e==null){
			e=cc.getCurrentElement(_name);
				if (e==null){
					ArrayList<Element> cArray=cc.getOldElements(_name);
					if (cArray.isEmpty()){
						return null;
					}
					e=cArray.get(cArray.size()-1);
					
				}
			}
		}
		else if (_type==Element.EVENT){
			EventContainer ec=aic.getEvents();
			ArrayList<Event> eArray=ec.getCurrentEvents(_name);
			if (eArray.isEmpty()){
				e=ec.getRecentEvent(_name);
			}
			else{
				e=eArray.get(eArray.size()-1);
			}
		}
		else if (_type==Element.PRIMITIVE){
			PrimitiveContainer pc=aic.getPrimitives();
			e=pc.getCurrentPrimitive(_name);
			if (e==null){
				e=pc.getOldPrimitive(_name);
			}
		}
		return e;
	}

	protected boolean obeys(Element e) {
		return _duration.check(e.getTimeInterval().getDuration());
	}
}

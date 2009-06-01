package dt.processor.kbta.ontology.defs.Patterns.PatternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.container.EventContainer;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.threats.DurationCondition;

public class PatternElementContext extends PatternElement {

	public PatternElementContext(int type, String name, int ordinal,
			DurationCondition duration) {
		super(type, name, ordinal, duration);
	}

	@Override
	public ArrayList<Element> getValid(AllInstanceContainer aic) {
		ArrayList<Element> ans=new ArrayList<Element>();
		ComplexContainer<Context> ec=aic.getContexts();
		Context e;
		ArrayList<Context> eArray=ec.getOldElements(_name);
		Context eNew=eArray.get(eArray.size()-1);
		if (eArray!=null){
			for (Context e1: eArray){
				if (obeys(e1)){
					ans.add(e1);
				}

				else{
					eArray.remove(e1);
				}
			}
		}
		e=ec.getCurrentElement(_name);
		if (e!=null && obeys(e)){
			ans.add(e);
		}		
		e=ec.getNewestElement(_name);
		if (e!=null && obeys(e)){
			ans.add(e);
		}
		if (ans.isEmpty()){
			return null;
		}
		return ans;
	}

}

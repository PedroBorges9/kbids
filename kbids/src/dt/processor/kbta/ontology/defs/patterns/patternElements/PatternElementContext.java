package dt.processor.kbta.ontology.defs.patterns.patternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;

public final class PatternElementContext extends PatternElement {

	public PatternElementContext(int type, String name, int ordinal,
			DurationCondition duration) {
		super(type, name, ordinal, duration);
	}

	@Override
	public ArrayList<Element> getValidElements(AllInstanceContainer aic) {
		ArrayList<Element> ans=new ArrayList<Element>();
		ComplexContainer<Context> ec=aic.getContexts();
		Context e;
		ArrayList<Context> eArray=ec.getOldElements(_name);
		if (eArray!=null){
			for (Context e1: eArray){
				if (check(e1)){
					ans.add(e1);
				}

				
			}
		}
		e=ec.getCurrentElement(_name);
		if (e!=null && check(e)){
			ans.add(e);
		}		
		e=ec.getNewestElement(_name);
		if (e!=null && check(e)){
			ans.add(e);
		}
		if (ans.isEmpty()){
			return null;
		}
		return ans;
	}

	@Override
	public ElementDef getElementDef(Ontology ontolgy){
		return ontolgy.getContextDef(_name);
	}

}

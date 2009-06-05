package dt.processor.kbta.ontology.defs.Patterns.PatternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Abstraction;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;

public class PatternElementTrend extends PatternElement {
	public SymbolicValueCondition _symbolicValueCondition;

	public PatternElementTrend(int type,String name, int ordinal,
			DurationCondition duration, SymbolicValueCondition symbolicValueCondition) {
		super(type,name, ordinal, duration);
		_symbolicValueCondition = symbolicValueCondition;
	}

	@Override
	public String toString(){
		return super.toString()+"  "+_symbolicValueCondition;
	}
	@Override
	protected boolean obeys(Element e) {
		return (super.obeys(e) && _symbolicValueCondition.check(((Abstraction) e).getValue()));
	}

	@Override
	public ArrayList<Element> getValidElements(AllInstanceContainer aic) {
		ArrayList<Element> ans=new ArrayList<Element>();
		ComplexContainer<Trend> ec=aic.getTrends();
		Trend e;
		ArrayList<Trend> eArray=ec.getOldElements(_name);
		if (eArray!=null){
			for (Trend e1: eArray){
				if (obeys(e1)){
					ans.add(e1);
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

	@Override
	public ElementDef getElementDef(Ontology ontology){
		return ontology.getTrendDef(_name);
	}


}

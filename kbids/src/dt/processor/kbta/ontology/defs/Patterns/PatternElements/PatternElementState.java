package dt.processor.kbta.ontology.defs.Patterns.PatternElements;

import java.util.ArrayList;

import android.util.Log;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.container.ComplexContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Abstraction;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;

public class PatternElementState extends PatternElement {
	public SymbolicValueCondition _symbolicValueCondition;

	public PatternElementState(int type,String name, int ordinal,
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
		Log.d("PatternCreation", "pattern element state obeys, is e null"+(e==null));
		return (super.obeys(e) && _symbolicValueCondition.check(((Abstraction) e).getValue()));
	}

	@Override
	public ArrayList<Element> getValid(AllInstanceContainer aic) {
		ArrayList<Element> ans=new ArrayList<Element>();
		ComplexContainer<State> ec=aic.getStates();
		State e;
	
		ArrayList<State> eArray=ec.getOldElements(_name);
		if (eArray!=null){
			for (State e1: eArray){
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
		return ontology.getStateDef(_name);
	}


}

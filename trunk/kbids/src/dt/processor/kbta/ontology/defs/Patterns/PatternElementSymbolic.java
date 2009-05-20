package dt.processor.kbta.ontology.defs.Patterns;

import java.util.HashSet;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;

public class PatternElementSymbolic extends PatternElement {
	public SymbolicValueCondition _symbolicValueCondition;

	public PatternElementSymbolic(int type,String name, int ordinal,
			DurationCondition duration, SymbolicValueCondition symbolicValueCondition) {
		super(type,name, ordinal, duration);
		_symbolicValueCondition = symbolicValueCondition;
	}
	
	@Override
	public String toString(){
		return super.toString()+"  "+_symbolicValueCondition;
	}
	
	@Override
	public Element getValid(AllInstanceContainer aic) {
		// TODO Auto-generated method stub
		return super.getValid(aic);
	}
}

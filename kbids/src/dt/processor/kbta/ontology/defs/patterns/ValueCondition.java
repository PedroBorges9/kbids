package dt.processor.kbta.ontology.defs.patterns;

import dt.processor.kbta.ontology.instances.Element;

public abstract class ValueCondition {
	
	public abstract boolean check(Element a, Element b);
	
	@Override
	public abstract String toString();
}

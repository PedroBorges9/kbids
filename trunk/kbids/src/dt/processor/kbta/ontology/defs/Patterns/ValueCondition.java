package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public abstract class ValueCondition {
	
	public abstract boolean obeys(Element a, Element b);
	@Override
	public abstract String toString();
}

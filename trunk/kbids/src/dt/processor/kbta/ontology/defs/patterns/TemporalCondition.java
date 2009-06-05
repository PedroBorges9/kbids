package dt.processor.kbta.ontology.defs.patterns;

import dt.processor.kbta.ontology.instances.Element;

public abstract class TemporalCondition {
	
	public abstract boolean check(Element a, Element b);


	public abstract String toString();

}

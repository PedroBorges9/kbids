package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public abstract class TemporalCondition {
	
	public abstract boolean Obeys(Element a, Element b);


	public abstract String toString();

}

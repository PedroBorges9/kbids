package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public class NoValueCondition extends ValueCondition {

	@Override
	public boolean obeys(Element a, Element b) {
		return true;
	}

}

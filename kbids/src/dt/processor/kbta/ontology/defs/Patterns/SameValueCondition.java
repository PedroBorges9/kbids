package dt.processor.kbta.ontology.defs.Patterns;

import dt.processor.kbta.ontology.instances.Element;

public class SameValueCondition extends ValueCondition {

	@Override
	public boolean obeys(Element a, Element b) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public String toString(){
		return "Same";
	}

}

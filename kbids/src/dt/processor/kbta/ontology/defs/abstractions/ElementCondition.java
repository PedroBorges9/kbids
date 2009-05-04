package dt.processor.kbta.ontology.defs.abstractions;

import dt.processor.kbta.ontology.instances.Element;

public abstract class ElementCondition {
	protected final String _name;
	
	public ElementCondition(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public abstract boolean checkValue(Element element); 
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	

}

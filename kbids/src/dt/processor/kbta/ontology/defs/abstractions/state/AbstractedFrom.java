package dt.processor.kbta.ontology.defs.abstractions.state;

import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;

public class AbstractedFrom{
	private final int _type;

	private final String _name;

	private final int _hashCode;

	public AbstractedFrom(int type, String name){
		_type = type;
		_name = name;
		_hashCode = (_type + _name).hashCode();
	}
	
	public String getName(){
		return _name;
	}

	public int getType(){
		return _type;
	}

	@Override
	public String toString(){
		return " type=" + Element.TYPES[_type] + " name=" + _name;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Element){
			Element element = (Element)o;
			return element.getName() == _name && element.getType() == _type;
		}else if (o instanceof AbstractedFrom){
			AbstractedFrom af = (AbstractedFrom)o;
			return (_type == af.getType() && _name.equals(af.getName()));
		}
		return false;
	}

	@Override
	public int hashCode(){
		return _hashCode;
	}
	
	public ElementDef getElementDef(Ontology ontology){
		ElementDef elementDef=null;
		switch (_type){
			case Element.PRIMITIVE:
				elementDef= ontology.getPrimitiveDef(_name);
				break;
			case Element.STATE:
				elementDef = ontology.getStateDef(_name);
				break;
			case Element.TREND:
				elementDef = ontology.getTrendDef(_name);
				break;
		}
		return elementDef;
	}

}

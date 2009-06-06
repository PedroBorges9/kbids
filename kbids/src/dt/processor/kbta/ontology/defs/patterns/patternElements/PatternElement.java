package dt.processor.kbta.ontology.defs.patterns.patternElements;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;

public abstract class PatternElement{
	protected int _type;

	protected String _name;

	protected int _ordinal;

	protected DurationCondition _duration;

	public PatternElement(int type, String name, int ordinal, DurationCondition duration){
		_type = type;
		_ordinal = ordinal;
		_duration = duration;
		_name = name;
	}

	@Override
	public String toString(){
		return " type=" + Element.TYPES[_type] + " name=" + _name + " ordinal= " + _ordinal;
	}

	public Integer getOrdinal(){
		return _ordinal;
	}

	public abstract ArrayList<Element> getValidElements(AllInstanceContainer aic);

	protected boolean check(Element e){		
		return (_duration == null) ? true : _duration.check(e.getTimeInterval().getDuration());
	}

	public int getType(){
		return _type;
	}

	public abstract ElementDef getElementDef(Ontology ontology);
}

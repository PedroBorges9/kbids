/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;


/**
 * @author 
 *
 */
public class Element {
	protected final ElementDef _elementDef;
	protected final String _name;
	protected final long _start;
	protected final long _end;
	
	public Element(ElementDef elementDef, String name,long start, long end){
		  _elementDef = elementDef;
		  _name = name;
		  _start = start;
		  _end = end;
	}
	
	public String getName() {
		return _name;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO compare by name and start and end
		return super.equals(o);
	}
}

/**
 * 
 */
package dt.processor.kbta.ontology;


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
}

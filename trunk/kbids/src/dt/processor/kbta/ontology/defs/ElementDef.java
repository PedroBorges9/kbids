/**
 * 
 */
package dt.processor.kbta.ontology.defs;

/**
 * @author 
 *
 */
public class ElementDef {
	protected final String _name;
	private int _lastCreated;
	
	public boolean assertNotCreatedIn(int iteration){
		return _lastCreated!=iteration;
	}

	public void setLastCreated(int iteration){
		_lastCreated = iteration;
	}

	public ElementDef(String name){
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	
	
}

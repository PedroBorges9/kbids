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
	protected int _lastCreated;
	
	public boolean assertNotCreatedIn(int iteration){
		return _lastCreated!=iteration;
	}

	public void setLastCreated(int iteration){
		_lastCreated = iteration;
	}

	public ElementDef(String name){
		_name = name;
		_lastCreated=-1;
	}
	
	public String getName() {
		return _name;
	}
	
	
	
}

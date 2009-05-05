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

	public ElementDef(String name){
		_name = name;
		_lastCreated=-1;
	}
	
	public final String getName() {
		return _name;
	}	
	
	public final boolean assertNotCreatedIn(int iteration){
		return _lastCreated!=iteration;
	}

	public final void setLastCreated(int iteration){
		_lastCreated = iteration;
	}
	
}

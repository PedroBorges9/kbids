/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import dt.processor.kbta.ontology.Ontology;

/**
 * @author
 */
public abstract class ElementDef{
	protected final String _name;

	private int _lastCreated;

	protected boolean _isMonitored;

	protected int _counter;


	
	public abstract void setInitiallyIsMonitored(Ontology ontology,boolean monitored);
	
	public abstract void setIsMonitored(Ontology ontology,boolean monitored);

	public ElementDef(String name){
		_name = name;
		_lastCreated = -1;
	}

	public final String getName(){
		return _name;
	}

	public final boolean assertNotCreatedIn(int iteration){
		return _lastCreated != iteration;
	}

	public final void setLastCreated(int iteration){
		_lastCreated = iteration;
	}

}

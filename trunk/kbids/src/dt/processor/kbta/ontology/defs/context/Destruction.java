/**
 * 
 */
package dt.processor.kbta.ontology.defs.context;

import dt.processor.kbta.container.AllInstanceContainer;

/**
 * @author 
 *
 */
public abstract class Destruction {
	protected final String _contextName;

	protected final String _elementName;


	public Destruction(String elementName, String contextName){
		_elementName = elementName;
		_contextName = contextName;
		
	}


	public abstract boolean destroy(AllInstanceContainer container);

	@Override
	public String toString(){
		return  "destruction of " + _contextName + "\nfrom " + _elementName;

	}
}

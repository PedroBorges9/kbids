/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;

/**
 * @author
 */
public abstract class AbstractionDef extends ElementDef{


	protected final String[] _necessaryContexts;

	public AbstractionDef(String name, ArrayList<String> necessaryContexts){
		super(name);
		_necessaryContexts = necessaryContexts.toArray(new String[necessaryContexts
				.size()]);
	}
	
	
	protected Element[] checkNecessaryContexts(AllInstanceContainer instances){
		Element[] elements = new Element[_necessaryContexts.length];
		int i = 0;
		for (String st : _necessaryContexts){
			Context context = instances.getContexts().getNewestElement(st);
			if (context != null){
				elements[i++] = context;
			}else if ((context = instances.getContexts().getCurrentElement(st)) != null){
				elements[i++] = context;
			}else{
				return null;
			}
		}
		return elements;
	}

}

/**
 * 
 */
package dt.processor.kbta.ontology.defs.context;

import java.util.List;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.defs.ElementDef;

/**
 * @author
 */
public final class ContextDef extends ElementDef{
	private final Induction[] _inductions;

	private final Destruction[] _destructions;

	public ContextDef(String name, List<Induction> inductions,
		List<Destruction> destructions){
		super(name);
		_inductions = inductions.toArray(new Induction[inductions.size()]);
		
		if (destructions == null){
			_destructions = null;	
		}else{
			_destructions = destructions.toArray(new Destruction[destructions.size()]);		
		}
	}

	public void createContext(AllInstanceContainer aic, int iteration){
		if (assertNotCreatedIn(iteration)){
			for (Induction induction : _inductions){
				if (induction.induce(aic)){
					setLastCreated(iteration);
					return;
				}
			}
		}
	}

	@Override
	public String toString(){
		return "contextDef " + _name + "\n inductions: " + _inductions;
	}
}

/**
 * 
 */
package dt.processor.kbta.ontology.defs.context;

import java.util.List;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.defs.ElementDef;


/**
 * @author 
 *
 */
public class ContextDef extends ElementDef {

	private List<Induction> _inductions;
	private List<Destruction> _destructions;

	public ContextDef(String name, List<Induction> inductions,
		List<Destruction> destructions){
		super(name);
		this._inductions = inductions;
		this._destructions = destructions;
	}

	public void createContext(AllInstanceContainer aic, int iteration){
		if (assertNotCreatedIn(iteration)){
			for (Induction i: _inductions){
				if (i.induct(aic)){
					_lastCreated=iteration;
					return;
				}
			}
		}
	}

	@Override
	public String toString(){
		return "contextDef "+ _name +"\n inductions: "+_inductions;
	}

}

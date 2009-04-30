/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.List;

import dt.processor.kbta.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Context;


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
	
	public void createContext(AllInstanceContainer aic){
		for (Induction i: _inductions){
			i.induct(aic);
		}
	}
	
	@Override
	public String toString(){
		// TODO Auto-generated method stub
		return "contextDef "+ _name +"\n inductions: "+_inductions;
	}

}

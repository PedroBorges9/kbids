/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.List;


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
	
	@Override
	public String toString(){
		// TODO Auto-generated method stub
		return "contextDef "+ _name +"\n inductions: "+_inductions;
	}

}

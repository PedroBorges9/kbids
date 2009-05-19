/**
 * 
 */
package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;
import java.util.HashMap;

import dt.processor.kbta.ontology.defs.ElementDef;

/**
 * @author 
 *
 */
public class LinearPatternDef extends ElementDef {
	private HashMap<Integer, PatternElement> _elements;
	private PairWiseCondition[] _pairConditions;
	public LinearPatternDef(String name, ArrayList<PairWiseCondition> pairConditions, 
			HashMap<Integer, PatternElement> elements ) {
		super(name);
		_elements=elements;
		_pairConditions=pairConditions.toArray(new PairWiseCondition[pairConditions.size()]);
	}
	
	
	@Override
	public String toString(){
		String st="LinearPattern\n"+"Elements\n";
		
		for(PatternElement pe : _elements.values()){
			st+=pe+"\n";	
		}
		st+="\n"+"PairWiseCondition\n";
		for(PairWiseCondition pwc : _pairConditions){
			st+=pwc+"\n";	
		}
		return st;
	}

}

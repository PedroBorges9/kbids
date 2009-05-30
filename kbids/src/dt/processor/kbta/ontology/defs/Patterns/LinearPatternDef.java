/**
 * 
 */
package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.Patterns.PatternElements.PatternElement;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Pattern;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author 
 *
 */
public class LinearPatternDef extends ElementDef {
	private PatternElement[] _elements;
//	HashMap<Integer, PatternElement> _elements;
	private PairWiseCondition[] _pairConditions;
	public LinearPatternDef(String name, ArrayList<PairWiseCondition> pairConditions, 
			HashMap<Integer, PatternElement> elements ) {
		super(name);
		_elements=elements.values().toArray(new PatternElement[elements.size()]);
		_pairConditions=pairConditions.toArray(new PairWiseCondition[pairConditions.size()]);
	} 
	
	public void createPattern(AllInstanceContainer aic){
		HashMap<Integer, ArrayList <Element>> elements=new HashMap<Integer, ArrayList <Element>>();
		long start=Long.MAX_VALUE;
		long end=0;
		for (PatternElement pe: _elements){
			Log.d("PatternCreation", "getting valid element" + pe.getOrdinal());
			ArrayList<Element> e=pe.getValid(aic);
			if (e==null){
				Log.d("PatternCreation", "no valid element" + pe.getOrdinal());
				return;
			}
			elements.put(pe.getOrdinal(), e);
//			TimeInterval eti=e.getTimeInterval();
//			if (eti.getStartTime()<start){
//				start=eti.getStartTime();
//			}
//			if (eti.getEndTime()>end){
//				end=eti.getEndTime();
//			}
		}
		for (PairWiseCondition pwc: _pairConditions){
			if (!pwc.obeys(elements.get(pwc.getFirst()),elements.get(pwc.getSecond()))){
				return;
			}
		}
		aic.addPattern(new Pattern(_name, new TimeInterval(start,end)));
	}
	
	
	@Override
	public String toString(){
		String st="LinearPattern\n"+"Elements\n";
		
		for(PatternElement pe : _elements){
			st+=pe+"\n";	
		}
		st+="\n"+"PairWiseCondition\n";
		for(PairWiseCondition pwc : _pairConditions){
			st+=pwc+"\n";	
		}
		return st;
	}

	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		visitor.visit(this);
		//TODO Traverse related elements (i.e. everyone in the pattern element array)
	}
}

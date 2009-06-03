/**
 * 
 */
package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

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
	private PairWiseCondition[] _pairConditions;
	private LinkedList<PartialPattern> _partials;
	private int _pairConditionsLastPointer;
	public LinearPatternDef(String name, ArrayList<PairWiseCondition> pairConditions, 
			HashMap<Integer, PatternElement> elements ) {
		super(name);
		_elements= new PatternElement[elements.size()];
		for (PatternElement e: elements.values()){
			_elements[e.getOrdinal()]=e;
		}
		rearrangePairs(pairConditions);
	} 

	private void rearrangePairs(ArrayList<PairWiseCondition> pairWiseConditions) {
		Log.d("PatternCreation", "rearranging");
		for (PairWiseCondition p: pairWiseConditions){
			Log.d("PatternCreation", p.toString());
		}
		int length = _elements.length;
		boolean[] used=new boolean[length];
		
		_pairConditions=new PairWiseCondition[pairWiseConditions.size()];
		PairWiseCondition[][] pwc=createSets(pairWiseConditions, length);
		_pairConditionsLastPointer=0;
		for (int i=0; i<length; i++){
			if (!used[i]){
				used[i]=true;
				rearrangeConditionsRecursive(length, used, i, i, pwc);
			}
		}
		Log.d("PatternCreation", "arranged to");
		for (PairWiseCondition p: _pairConditions){
			Log.d("PatternCreation", p.toString());
		}		
	}

	private PairWiseCondition[][] createSets(
		ArrayList<PairWiseCondition> pairWiseConditions , int length) {
		PairWiseCondition[][] sets=new PairWiseCondition[length][length];
		for (PairWiseCondition p: pairWiseConditions){
			sets[p.getFirst()][p.getSecond()]=p;
			sets[p.getSecond()][p.getFirst()]=p;
		}
		return sets;
	}

	private void rearrangeConditionsRecursive(int length, boolean[] used,
			int current, int from, PairWiseCondition[][] pwc) {
		Log.d("PatternCreation", "i: " + current + " from "+from);
		for (int j=0; j<length; j++){
			if (j==from){
				continue;
			}
			if (used[j]){
				PairWiseCondition pairWiseCondition = pwc[current][j];
				if (pairWiseCondition!=null){
					Log.d("PatternCreation", "_pairConditionsLastPointer "+_pairConditionsLastPointer);
					_pairConditions[_pairConditionsLastPointer]=pairWiseCondition;
					_pairConditionsLastPointer++;
				}
			}
		}
		for (int j=0; j<length; j++){
			if (j==from || used[j]){
				continue;
			}
			PairWiseCondition p=pwc[current][j];
			if (p==null){
				continue;
			}
			used[j]=true;
			_pairConditions[_pairConditionsLastPointer]=pwc[current][j];
			_pairConditionsLastPointer++;
			rearrangeConditionsRecursive(length, used, j, current, pwc);
		}
	}

	@SuppressWarnings("unchecked")
	public void createPattern(AllInstanceContainer aic){
		ArrayList <Element>[] elements=new ArrayList[_elements.length];

		for (PatternElement pe: _elements){
			Log.d("PatternCreation", "getting valid element " + pe.getOrdinal() + " for pattern "+ _name);
			ArrayList<Element> e=pe.getValid(aic);
			if (e==null){
				Log.d("PatternCreation", "no valid element " + pe.getOrdinal() + " for pattern "+ _name);
				return;
			}

			elements[pe.getOrdinal()]= e;


		}
		_partials=new LinkedList<PartialPattern>();
		for (Element e: elements[0]){
			PartialPattern pp=new PartialPattern(elements.length, e);
			_partials.add(pp);
		}
		for (PairWiseCondition pwc: _pairConditions){
			int first=pwc.getFirst();
			int second=pwc.getSecond();

			PartialPattern ppTemp=_partials.get(0);
			if (ppTemp.getElement(first)==null){
				if (ppTemp.getElement(second)==null){
					bothElementsMissing(pwc,elements[first], elements[second]);
				}
				else{
					firstElementMissing(pwc,elements[first]);
				}
			}
			else if (ppTemp.getElement(second)==null){
				secondElementMissing(pwc,elements[second]);
			}
			else{
				noElementsMissing(pwc);
			}
			if (_partials.isEmpty()){
				Log.d("PatternCreation", "no element which allow a sequence of pairWiseConditions for linear pattern "+_name);
				return;
			}


			//			if (!pwc.obeys(elements.get(pwc.getFirst()),elements.get(pwc.getSecond()))){
			//			return;
			//			}
		}

		PartialPattern last=_partials.get(_partials.size()-1);
		Pattern ans=last.toPattern(elements, _name);	
		aic.addPattern(ans);
	}




	private void noElementsMissing(PairWiseCondition pwc){
		ListIterator<PartialPattern> lIter=_partials.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp=lIter.next();
			if (!pwc.obeys(pp.getElement(pwc.getFirst()), 
					pp.getElement(pwc.getSecond()))){
				lIter.remove();
			}
		}

	}

	private void secondElementMissing(PairWiseCondition pwc, ArrayList<Element> secondElements){
		ListIterator<PartialPattern> lIter=_partials.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp=lIter.next();
			lIter.remove();
			for (Element e: secondElements){
				if (pwc.obeys(pp.getElement(pwc.getFirst()), e)){
					lIter.add(pp.addElement(pwc.getSecond(), e));
				}
			}

		}

	}

	private void firstElementMissing(PairWiseCondition pwc, ArrayList<Element> firstElements){
		ListIterator<PartialPattern> lIter=_partials.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp=lIter.next();
			lIter.remove();
			for (Element e: firstElements){
				if (pwc.obeys(e, pp.getElement(pwc.getSecond()))){
					lIter.add(pp.addElement(pwc.getFirst(), e));
				}
			}

		}

	}

	private void bothElementsMissing(PairWiseCondition pwc, ArrayList<Element> firstElements,
			ArrayList<Element> secondElements){
		ListIterator<PartialPattern> lIter=_partials.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp=lIter.next();
			lIter.remove();
			for (Element e1: firstElements){
				for (Element e2: secondElements){
					if (pwc.obeys(e1, e2)){
						lIter.add(pp.addTwoElements(pwc.getFirst(), e1, pwc.getSecond(), e2));
					}
				}
			}


		}
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
		for (PatternElement pe : _elements){
			pe.getElementDef(ontology).accept(ontology, visitor);
		}
		//TODO Traverse related elements (i.e. everyone in the pattern element array)
	}
}
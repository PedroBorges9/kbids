package dt.processor.kbta.ontology.defs.Patterns;

import java.util.ArrayList;
import java.util.Iterator;

import dt.processor.kbta.ontology.instances.Element;


public class PairWiseCondition {



	private int _first;
	private int _second;
	private int _value;
	private TemporalCondition _temporal;

	public PairWiseCondition(int first, int second, int valueCondition,
			TemporalCondition temporal) {
		_first = first;
		_second = second;
		_value = valueCondition;
		_temporal = temporal;
	}

	public int getFirst() {
		return _first;
	}



	public int getSecond() {
		return _second;
	}


	@Override
	public String toString(){	
		return " first= "+_first+" second= "+_second+" value= "+_value+" temporal= "+_temporal;
	}

	public boolean obeys(ArrayList<Element> elements1, ArrayList<Element> elements2) {
		boolean[] elements2Used=new boolean [elements2.size()];	

		Iterator<Element> eIter =elements1.iterator();
		while (eIter.hasNext()){
			int i=0;
			Element e1=eIter.next();
			boolean element1Used=false;
			Iterator<Element> e2Iter =elements2.iterator();
			while (e2Iter.hasNext()){

				Element e2=e2Iter.next();
				if (_temporal.Obeys(e1, e2)){
					switch (_value) {
					case 0:
						element1Used=true;
						elements2Used[i]=true;
						break;
					case 1:
						if (e1.compareTo(e2)<0){
							element1Used=true;
							elements2Used[i]=true;
						}
						break;
					case 2:
						if (e1.compareTo(e2)==0){
							element1Used=true;
							elements2Used[i]=true;
						}
						break;
					case 3:
						if (e1.compareTo(e2)>0){
							element1Used=true;
							elements2Used[i]=true;
						}
						break;
					default:
						break;
					}
					i++;

				}
			}
			if (!element1Used){
				eIter.remove();
			}
		}
		
		for (int i=elements2Used.length-1; i>=0; i--){
			if (elements2Used[i]=false){
				elements2.remove(i);
			}
		}	
		return ( !elements1.isEmpty() && !elements2.isEmpty());
	}


}

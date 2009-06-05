package dt.processor.kbta.ontology.defs.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import android.util.Log;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElement;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Pattern;

public final class LinearPatternDef extends ElementDef{
	private final PatternElement[] _elements;

	private final PairWiseCondition[] _pwcs;

	private LinkedList<PartialPattern> _partialPatterns;

	private static final boolean patternDebug = false;

	public LinearPatternDef(String name, ArrayList<PairWiseCondition> pwcs,
		HashMap<Integer, PatternElement> elements){
		super(name);
		_elements = new PatternElement[elements.size()];
		for (PatternElement e : elements.values()){
			_elements[e.getOrdinal()] = e;
		}

		_pwcs = rearrangePwcGraph(_elements.length, pwcs);
	}

	@SuppressWarnings("unchecked")
	public void createPattern(AllInstanceContainer aic){
		ArrayList<Element>[] elements = new ArrayList[_elements.length];

		for (PatternElement pe : _elements){
			Log.d("PatternCreation", "getting valid element " + pe.getOrdinal()
					+ " for pattern " + _name);
			ArrayList<Element> e = pe.getValidElements(aic);
			if (e == null){
				Log.d("PatternCreation", "no valid element " + pe.getOrdinal()
						+ " for pattern " + _name);
				return;
			}

			elements[pe.getOrdinal()] = e;

		}
		_partialPatterns = new LinkedList<PartialPattern>();
		int initElementOrdinal = 0;
		for (Element e : elements[initElementOrdinal]){
			PartialPattern pp = new PartialPattern(elements.length, initElementOrdinal, e);
			_partialPatterns.add(pp);
		}
		for (PairWiseCondition pwc : _pwcs){
			int first = pwc.getFirst();
			int second = pwc.getSecond();

			PartialPattern ppTemp = _partialPatterns.get(0);
			if (ppTemp.getElement(first) == null){
				if (ppTemp.getElement(second) == null){
					bothElementsMissing(pwc, elements[first], elements[second]);
				}else{
					firstElementMissing(pwc, elements[first]);
				}
			}else if (ppTemp.getElement(second) == null){
				secondElementMissing(pwc, elements[second]);
			}else{
				noElementsMissing(pwc);
			}
			if (_partialPatterns.isEmpty()){
				Log.d("PatternCreation",
					"no element which allow a sequence of pairWiseConditions for linear pattern "
							+ _name);
				return;
			}
		}

		PartialPattern last = _partialPatterns.get(_partialPatterns.size() - 1);
		Pattern ans = last.toPattern(elements, _name);
		aic.addPattern(ans);
	}

	private void noElementsMissing(PairWiseCondition pwc){
		ListIterator<PartialPattern> lIter = _partialPatterns.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp = lIter.next();
			if (!pwc.doElementsComply(pp.getElement(pwc.getFirst()), pp.getElement(pwc.getSecond()))){
				lIter.remove();
			}
		}

	}

	private void secondElementMissing(PairWiseCondition pwc,
		ArrayList<Element> secondElements){
		ListIterator<PartialPattern> lIter = _partialPatterns.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp = lIter.next();
			lIter.remove();
			for (Element e : secondElements){
				if (pwc.doElementsComply(pp.getElement(pwc.getFirst()), e)){
					lIter.add(pp.addElement(pwc.getSecond(), e));
				}
			}

		}
	}

	private void firstElementMissing(PairWiseCondition pwc,
		ArrayList<Element> firstElements){
		ListIterator<PartialPattern> lIter = _partialPatterns.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp = lIter.next();
			lIter.remove();
			for (Element e : firstElements){
				if (pwc.doElementsComply(e, pp.getElement(pwc.getSecond()))){
					lIter.add(pp.addElement(pwc.getFirst(), e));
				}
			}

		}
	}

	private void bothElementsMissing(PairWiseCondition pwc,
		ArrayList<Element> firstElements, ArrayList<Element> secondElements){
		ListIterator<PartialPattern> lIter = _partialPatterns.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp = lIter.next();
			lIter.remove();
			for (Element e1 : firstElements){
				for (Element e2 : secondElements){
					if (pwc.doElementsComply(e1, e2)){
						lIter.add(pp.addTwoElements(pwc.getFirst(), e1, pwc.getSecond(),
							e2));
					}
				}
			}
		}
	}
	
	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		visitor.visit(this);
		for (PatternElement pe : _elements){
			pe.getElementDef(ontology).accept(ontology, visitor);
		}
	}
	
	/**
	 * If to view the elements as nodes and the PWCs are bi-directional edges connecting
	 * them, rearranging the Pair-Wise Conditions so that:
	 * <ol>
	 * <li> An entire connected component will be traversed prior to moving to another one
	 * <li> Within the connected component cycles will be traversed as soon as possible
	 * </ol>
	 * 
	 * @param nodeCount The number of nodes/elements
	 * @param pwcEdges The edges/pair-wise conditions
	 * @return The pair-wise conditions according to the aforementioned order
	 */
	private static PairWiseCondition[] rearrangePwcGraph(int nodeCount,
		ArrayList<PairWiseCondition> pwcEdges){
		if (patternDebug)
			for (PairWiseCondition p : pwcEdges){
				Log.d("PatternCreation", p.toString());
			}

		// Representing the edges as an adjencency matrix
		PairWiseCondition[][] adjacencyMatrix = new PairWiseCondition[nodeCount][nodeCount];
		for (PairWiseCondition p : pwcEdges){
			adjacencyMatrix[p.getFirst()][p.getSecond()] = p;
			adjacencyMatrix[p.getSecond()][p.getFirst()] = p;
		}

		// A list for the nodes to be placed in the new order
		ArrayList<PairWiseCondition> rearrangedNodes = new ArrayList<PairWiseCondition>(
				pwcEdges.size());
		// The nodes already traversed
		boolean[] used = new boolean[nodeCount];

		// Traversing all of the connected components of the PWC graph
		// and rearranging each one
		for (int i = 0; i < nodeCount; i++){
			if (!used[i]){
				used[i] = true;
				rearrangeConnectedComponent(nodeCount, used, i, i, adjacencyMatrix,
					rearrangedNodes);
			}
		}
		if (patternDebug){
			Log.d("PatternCreation", "arranged to");
			for (PairWiseCondition p : rearrangedNodes){
				Log.d("PatternCreation", p.toString());
			}
		}

		return rearrangedNodes.toArray(new PairWiseCondition[rearrangedNodes.size()]);
	}

	/**
	 * Rearranges a single connected component so that cycles will be traversed as soon as
	 * possible
	 */
	private static void rearrangeConnectedComponent(int nodeCount, boolean[] used,
		int current, int from, PairWiseCondition[][] adjencencyMatrix,
		ArrayList<PairWiseCondition> rearrangedNodes){
		for (int j = 0; j < nodeCount; j++){
			if (j == from){
				continue;
			}
			if (used[j]){
				PairWiseCondition pairWiseCondition = adjencencyMatrix[current][j];
				if (pairWiseCondition != null){
					rearrangedNodes.add(pairWiseCondition);
				}
			}
		}
		for (int j = 0; j < nodeCount; j++){
			if (j == from || used[j]){
				continue;
			}
			PairWiseCondition p = adjencencyMatrix[current][j];
			if (p == null){
				continue;
			}
			used[j] = true;
			rearrangedNodes.add(adjencencyMatrix[current][j]);
			rearrangeConnectedComponent(nodeCount, used, j, current, adjencencyMatrix,
				rearrangedNodes);
		}
	}

	@Override
	public String toString(){
		String st = "LinearPattern\n" + "Elements\n";

		for (PatternElement pe : _elements){
			st += pe + "\n";
		}
		st += "\n" + "PairWiseCondition\n";
		for (PairWiseCondition pwc : _pwcs){
			st += pwc + "\n";
		}
		return st;
	}
}

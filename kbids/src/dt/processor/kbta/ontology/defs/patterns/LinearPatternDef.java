package dt.processor.kbta.ontology.defs.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElement;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.LinearPattern;

public final class LinearPatternDef extends ElementDef{
	private final PatternElement[] _elements;

	private final PairWiseCondition[] _pwcs;

	private LinkedList<PartialPattern> _partialPatterns;

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
			ArrayList<Element> e = pe.getValidElements(aic);
			if (e == null){
//				android.util.Log.d("PatternCreation", "Missing element [" + pe.getOrdinal() + "] " + pe);
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
				return;
			}
		}

		PartialPattern last = _partialPatterns.get(_partialPatterns.size() - 1);
		LinearPattern ans = last.toPattern(elements, _name);
		aic.addPattern(ans);
	}

	private void noElementsMissing(PairWiseCondition pwc){
		ListIterator<PartialPattern> lIter = _partialPatterns.listIterator();
		while (lIter.hasNext()){
			PartialPattern pp = lIter.next();
			if (!pwc.check(pp.getElement(pwc.getFirst()), pp.getElement(pwc.getSecond()))){
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
				if (pwc.check(pp.getElement(pwc.getFirst()), e)){
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
				if (pwc.check(e, pp.getElement(pwc.getSecond()))){
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
					if (pwc.check(e1, e2)){
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
			ElementDef elementDef = pe.getElementDef(ontology);
			if (elementDef == null){
				throw new IllegalStateException("Undefined element: " + pe);
			}
			elementDef.accept(ontology, visitor);
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
		return "LinearPattern: " + _name;
	}
}

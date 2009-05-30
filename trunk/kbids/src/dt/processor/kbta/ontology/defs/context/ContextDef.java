/**
 * 
 */
package dt.processor.kbta.ontology.defs.context;

import java.util.Arrays;
import java.util.List;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.ElementDef.ElementVisitor;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractedFrom;

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

	public void destroyContext(AllInstanceContainer aic, int iteration){
		if (assertNotCreatedIn(iteration)){
			if (aic.getContexts().getCurrentElement(_name) != null){
				for (Destruction destruction : _destructions){
					if (destruction.destroy(aic)){
						setLastCreated(iteration);
						return;
					}
				}
			}
		}
	}
	
	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		visitor.visit(this);
		for (Induction induction : _inductions){
			ElementDef elementDef = induction.getElementDef(ontology);
			elementDef.accept(ontology, visitor);
		}
	}

	@Override
	public String toString(){
		return "<Context name=" + _name + "\n inductions: "
				+ Arrays.toString(_inductions) + "\n destructions: "
				+ Arrays.toString(_destructions) + " isMonitored=" + _isMonitored
				+ " counter=" + _monitoredCounter + "/>\n";
	}
}

package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Element;

public abstract class GeneratedFrom {
	protected final String _elementName;
	protected final SymbolicValueCondition _symbolicValueCondition;
	protected final DurationCondition _durationCondition;

	public GeneratedFrom(String name,
			SymbolicValueCondition symbolicValueCondition,
			DurationCondition durationCondition) {
		_elementName = name;
		_symbolicValueCondition = symbolicValueCondition;
		_durationCondition = durationCondition;
	}
	
	public abstract Element locateMatchingElement(AllInstanceContainer allInstances);

	public final String getName() {
		return _elementName;
	}
	
	public final long getMinDuration(){
		return _durationCondition.getMinDuration();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(_elementName).append(" (");
		if (_symbolicValueCondition != null){
			sb.append(_symbolicValueCondition).append(", ");
		}
		sb.append(_durationCondition).append(")");
		return sb.toString();
	}

}

package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;

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
	
	public abstract boolean matchConditions(AllInstanceContainer allInstances);

	public final String getName() {
		return _elementName;
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

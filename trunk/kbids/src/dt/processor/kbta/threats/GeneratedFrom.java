package dt.processor.kbta.threats;

import dt.processor.kbta.container.AllInstanceContainer;

public abstract class GeneratedFrom {
	protected final String _name;
	protected final SymbolicValueCondition _symbolicValueCondition;
	protected final DurationCondition _durationCondition;

	public GeneratedFrom(String name,
			SymbolicValueCondition symbolicValueCondition,
			DurationCondition durationCondition) {
		_name = name;
		_symbolicValueCondition = symbolicValueCondition;
		_durationCondition = durationCondition;

	}

	public SymbolicValueCondition getSymbolicValueCondition() {
		return _symbolicValueCondition;
	}

	public DurationCondition getDurationCondition() {
		return _durationCondition;
	}



	public String getName() {
		return _name;
	}

	
	public abstract boolean matchConditions(AllInstanceContainer allInstances);

	@Override
	public String toString() {
		String st = "GeneratedFrom\n";
		return st;
	}

}

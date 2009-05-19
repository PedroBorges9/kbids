package dt.processor.kbta.threats;

import java.util.Arrays;
import java.util.HashSet;

public final class SymbolicValueCondition{
	private final HashSet<String> _symbolicValueConditions;

	public SymbolicValueCondition(HashSet<String> symbolicValueConditions){
		_symbolicValueConditions = symbolicValueConditions;
	}

	public boolean check(String elementValue){
		return _symbolicValueConditions.contains(elementValue);
	}

	@Override
	public String toString(){
		return "symbolicValueConditions= " + Arrays.toString(_symbolicValueConditions.toArray());
	}

}

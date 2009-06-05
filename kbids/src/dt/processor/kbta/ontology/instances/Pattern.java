package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

public final class Pattern extends Element{

	public Pattern(String name, TimeInterval timeInterval, Bundle extras){
		super(PATTERN, name, timeInterval, extras);
	}

	@Override
	public int compareTo(Element another){
		return 0;
	}
}

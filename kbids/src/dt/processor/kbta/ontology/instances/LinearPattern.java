package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

public final class LinearPattern extends Element{

	public LinearPattern(String name, TimeInterval timeInterval, Bundle extras){
		super(LINEAR_PATTERN, name, timeInterval, extras);
	}

	@Override
	public int compareTo(Element another){
		return 0;
	}
}

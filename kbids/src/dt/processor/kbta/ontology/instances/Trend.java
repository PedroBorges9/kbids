/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public final class Trend extends Abstraction{
	private Primitive _first;

	private Primitive _last;

	private String _type;

	public Trend(String name, String value, TimeInterval timeInterval, Bundle extras,
		Primitive first, Primitive last, String type){
		super(TREND, name, value, timeInterval, extras);
		_first = first;
		_last = last;
		_type = type;

	}

	public void setFirst(Primitive first){
		_first = first;
	}

	public void setLast(Primitive last){
		_last = last;
	}

}

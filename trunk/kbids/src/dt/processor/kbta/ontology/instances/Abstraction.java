/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.util.TimeInterval;

/**
 *
 */
public abstract class Abstraction extends Element{
	protected String _value;

	public Abstraction(int type, String name, String value, TimeInterval timeInterval, Bundle extras){
		super(type, name, timeInterval, extras);
		_value = value;
	}

	public final String getValue(){
		return _value;
	}

	public void setValue(String value){
		_value = value;
	}
	@Override
	public String toString(){
		return super.toString() + " value=" + _value;
	}
}

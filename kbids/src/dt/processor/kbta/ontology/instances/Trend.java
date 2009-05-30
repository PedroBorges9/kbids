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


	public Trend(String name, String value, TimeInterval timeInterval, Bundle extras,
		Primitive first, Primitive last){
		super(TREND, name, value, timeInterval, extras);
		_first = first;
		_last = last;
		

	}


	public void setLast(Primitive last){
		_last = last;
	}
	
	
	
	public Primitive getLast(){
		return _last;
	}
	
	public Primitive getFirst(){
		return _first;
	}


	@Override
	public int compareTo(Element another) {
		return 0;
	}

}

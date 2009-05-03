/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.util.TimeInterval;


/**
 * @author 
 *
 */
public class Element {
	protected final String _name;
	protected  TimeInterval _timeInterval;
	
	public static final int PRIMITIVE=0;
	public static final int EVENT=1;
	public static final int CONTEXT=2;
	public static final int STATE=3;
	public static final int TREND=4;
	public static final int PATTERN=5;
	
	public Element( String name,TimeInterval timeInterval){
		  _name = name;
		  _timeInterval = timeInterval;

	}
	
	public String getName() {
		return _name;
	}
	
	
	
	

	public TimeInterval getTimeInterval() {
		return _timeInterval;
	}



	@Override
	public boolean equals(Object o) {
		Element element=(Element)o;
		return element.getName()==_name && element.getTimeInterval().equals(_timeInterval);
	}

	public void setInterval(TimeInterval ti){
		_timeInterval = ti;
	}
	
	
	
	
}

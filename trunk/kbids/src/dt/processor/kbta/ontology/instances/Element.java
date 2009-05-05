/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.defs.abstractions.AbstractedFrom;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public abstract class Element{
	protected final String _name;

	protected TimeInterval _timeInterval;

	protected int _type;

	public static final int PRIMITIVE = 0;

	public static final int EVENT = 1;

	public static final int CONTEXT = 2;

	public static final int STATE = 3;

	public static final int TREND = 4;

	public static final int PATTERN = 5;

	private final int _hashCode;

	public Element(int type, String name, TimeInterval timeInterval){
		_name = name;
		_timeInterval = timeInterval;
		_type = type;
		_hashCode = (_type + _name).hashCode();

	}

	public String getName(){
		return _name;
	}

	public TimeInterval getTimeInterval(){
		return _timeInterval;
	}

	public int getType(){
		return _type;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Element){
			Element element = (Element)o;
			return element.getName() == _name && element._type == _type
					&& element.getTimeInterval().equals(_timeInterval);
		}else if (o instanceof AbstractedFrom){
			AbstractedFrom af = (AbstractedFrom)o;
			return (_type == af.getType() && _name.equals(af.getName()));
		}
		return false;
	}

	public void setInterval(TimeInterval ti){
		_timeInterval = ti;
	}

	@Override
	public int hashCode(){
		return _hashCode;
	}

	@Override
	public String toString(){
		return _name + " " + _timeInterval;
	}

}

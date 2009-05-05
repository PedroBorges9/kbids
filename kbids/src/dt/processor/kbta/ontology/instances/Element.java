/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.abstractions.AbstractedFrom;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public abstract class Element{

	public static final int PRIMITIVE = 0, EVENT = 1, CONTEXT = 2, STATE = 3, TREND = 4,
			PATTERN = 5;

	protected final String _name;

	protected final TimeInterval _timeInterval;

	protected int _type;

	private final int _hashCode;

	public Element(int type, String name, long start, long end){
		this(type, name, new TimeInterval(start, end));
	}

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

	@Override
	public int hashCode(){
		return _hashCode;
	}

	@Override
	public String toString(){
		return _name + " " + _timeInterval;
	}

}

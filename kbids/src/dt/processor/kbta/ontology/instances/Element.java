/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import android.os.Bundle;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractedFrom;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public abstract class Element implements Comparable<Element>{
	/** Mapping of element types to strings, only used for debugging */
	public static final String[] TYPES = {"Primitive", "Event", "Context", "State", "Trend", "LinearPattern"};
	public static final int PRIMITIVE = 0, EVENT = 1, CONTEXT = 2, STATE = 3, TREND = 4,
			LINEAR_PATTERN = 5;

	protected final String _name;

	protected final int _type;

	private final TimeInterval _timeInterval;

	private final int _hashCode;

	private final Bundle _extras;

	public Element(int type, String name, long start, long end, Bundle extras){
		this(type, name, new TimeInterval(start, end), extras);
	}

	public Element(int type, String name, TimeInterval timeInterval, Bundle extras){
		_name = name;
		_timeInterval = timeInterval;
		_type = type;
		_hashCode = (_type + _name).hashCode();
		_extras = extras;
	}

	public final String getName(){
		return _name;
	}

	public final TimeInterval getTimeInterval(){
		return _timeInterval;
	}

	public final int getType(){
		return _type;
	}

	public final Bundle getExtras(){
		return _extras;
	}

	public final void addInnerExtras(Bundle dest){
		if (_extras != null && dest != null){
			dest.putAll(_extras);
		}
	}

	public final void addToInnerExtras(Bundle src){
		if (_extras != null && src != null){
			_extras.putAll(src);
		}
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
	public final int hashCode(){
		return _hashCode;
	}

	@Override
	public String toString(){
		String type = null;
		switch (_type){
			case PRIMITIVE:
				type = "Primitive";
				break;
			case EVENT:
				type = "Event";
				break;
			case CONTEXT:
				type = "Context";
				break;
			case STATE:
				type = "State";
				break;
			case TREND:
				type = "Trend";
				break;
			case LINEAR_PATTERN:
				type = "Pattern";
				break;
		}
		return type + " " + _name + " " + _timeInterval
				+ ((_extras == null || _extras.isEmpty()) ? "" : " " + _extras)
				;
	}

}

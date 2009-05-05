/**
 * 
 */
package dt.processor.kbta.ontology.defs.context;

import java.util.ArrayList;

import android.util.Log;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.util.TimeInterval;
import static dt.processor.kbta.KBTAProcessorService.TAG;
/**
 * @author 
 *
 */
public class Induction {

	private int _type;
	private String _name;
	private String _from;
	private String _symbolicValue;
	private String _relative;
	private long _gap;
	private NumericRange _numericValues;
	public Induction(int type, String from, String name, String symbolicValue, NumericRange numericValues, String relative, Long gap){
		this._type = type;
		this._from = from;
		_name=name;
		_symbolicValue = symbolicValue;
		_numericValues = numericValues;
		_relative=relative;
		_gap=gap;
	}

	public boolean induct(AllInstanceContainer container){
		switch (_type){
		case Element.PRIMITIVE:
			return inductFromPrimitive(container);
		case Element.EVENT:
			return inductFromEvent(container);
		case Element.STATE:
			return inductFromState(container);
		case Element.TREND:
			return inductFromTrend(container);
		default:
			Log.e(TAG,"ILLEGAL TYPE ( "+_type+" )TO INDUCT CONTEXT "+_name+" NOT SUPPOSED TO HAPPEN");
		return false;
		}

	}

	private boolean inductFromTrend(AllInstanceContainer container){
		long start=-1;
		long end=-1;
		boolean create=false;
		Trend t=container.getTrends().getNewestElement(_from);
		if (t!=null){
			if (_symbolicValue.equalsIgnoreCase(t.getValue())){
				create=true;
				start=t.getTimeInterval().getStartTime();
				if (_relative.equalsIgnoreCase("Start")) {
					end=t.getTimeInterval().getStartTime()+_gap;
				}
				else{
					end=t.getTimeInterval().getEndTime()+_gap;
				}
			}
			if (create){
				return (createContext(container, start, end));
			}
		}
		return false;
	}

	private boolean inductFromState(AllInstanceContainer container){
		long start=-1;
		long end=-1;
		boolean create=false;
		State s=container.getStates().getNewestElement(_from);
		if (s!=null){
			if (_symbolicValue.equalsIgnoreCase(s.getValue())){
				create = true;
				start = s.getTimeInterval().getStartTime();
				if (_relative.equalsIgnoreCase("Start")){
					end = s.getTimeInterval().getStartTime() + _gap;
				}
				else{
					end = s.getTimeInterval().getEndTime() + _gap;
				}
			}
			if (create){
				return (createContext(container, start, end));
			}
		}
		return false;
	}

	private boolean inductFromEvent(AllInstanceContainer container){
		ArrayList<Event> events=container.getEvents().getCurrentEvent(_from);
		boolean created=false;
		if (events!=null){
			for (Event e : events){
				long start = -1;
				long end = -1;
				start = e.getTimeInterval().getStartTime();
				if (_relative.equalsIgnoreCase("Start")){
					end = e.getTimeInterval().getStartTime() + _gap;
				}
				else{
					end = e.getTimeInterval().getEndTime() + _gap;
				}
				if (createContext(container, start, end)){
					created=true;
				}
			}
		}
		return false;
	}

	private boolean inductFromPrimitive(AllInstanceContainer container){
		long start=-1;
		long end=-1;
		boolean create=false;
		Primitive p=container.getPrimitives().getCurrentPrimitive(_from);
		if (p!=null){
			if (_numericValues.inRange(p.getValue())){
				end = p.getTimeInterval().getEndTime() + _gap;
				start = p.getTimeInterval().getEndTime();
				create = true;
			}
			if (create){
				return (createContext(container, start, end));
			}
		}
		return false;
	}

	private boolean createContext(AllInstanceContainer container, long start, long end){
		Context context=container.getContexts().getCurrentElement(_name);
		if (context!=null){
			long contextEnd=context.getTimeInterval().getEndTime();
			if (contextEnd<start){
				container.addContext( new Context( _name, new TimeInterval(start, end)));
				return true;
			}
			else if(contextEnd<end){
				context.getTimeInterval().setEndTime(end);
				return false;
			}
			return false;
		}
		else {
			container.addContext( new Context( _name, new TimeInterval(start, end)));
			return true;
		}

	}

	@Override
	public String toString(){
		String ans="induction of "+_name+"\nfrom "+_from+" of type"+ _type+" with the value";
		if (_type==Element.PRIMITIVE){
			ans=ans+_numericValues;
		}
		else if (!(_type==Element.EVENT)){
			ans=ans+_symbolicValue;
		}
		return ans+"\nthat ends at "+_gap+" miliseconds after the originator "+_relative;

	}
}

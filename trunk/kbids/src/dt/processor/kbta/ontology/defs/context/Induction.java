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

	public void induct(AllInstanceContainer container){
		switch (_type){
		case Element.PRIMITIVE:
			inductFromPrimitive(container);
			break;
		case Element.EVENT:
			inductFromEvent(container);
			break;	
		case Element.STATE:
			inductFromState(container);
			break;
		case Element.TREND:
			inductFromTrend(container);
			break;
		default:
			Log.e(TAG,"ILLEGAL TYPE ( "+_type+" )TO INDUCT CONTEXT "+_name+" NOT SUPPOSED TO HAPPEN");
		break;
		}

	}

	private void inductFromTrend(AllInstanceContainer container){
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
				createContext(container, start, end);
			}
		}
	}

	private void inductFromState(AllInstanceContainer container){
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
				createContext(container, start, end);
			}
		}
	}

	private void inductFromEvent(AllInstanceContainer container){
		ArrayList<Event> events=container.getEvents().getCurrentEvent(_from);//TODO//
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
				createContext(container, start, end);
			}
		}
	}

	private void inductFromPrimitive(AllInstanceContainer container){
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
				createContext(container, start, end);
			}
		}
	}

	private void createContext(AllInstanceContainer container, long start, long end){
		Context context=container.getContexts().getCurrentElement(_name);
		
		if (context!=null){
			long contextEnd=context.getTimeInterval().getEndTime();
			if (contextEnd<start){
				container.addContext( new Context( _name, new TimeInterval(start, end)));
			}
			else if(contextEnd<end){
				context.getTimeInterval().setEndTime(end);
			}
		}
		else {
			container.addContext( new Context( _name, new TimeInterval(start, end)));
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

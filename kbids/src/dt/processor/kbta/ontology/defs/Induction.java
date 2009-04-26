/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import android.text.Html.TagHandler;
import android.util.Log;
import dt.processor.kbta.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.State;
import dt.processor.kbta.ontology.instances.Trend;

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

	public Context induct(AllInstanceContainer container){
		long start=-1;
		long end=-1;
		boolean create=false;
		switch (_type){
			case Element.PRIMITIVE:
				Primitive p=container.getPrimitives().getMostRecent(_from);
				if (_numericValues.inRange(p.getValue())){
					end=p.getEnd()+_gap;
					start=p.getEnd();
					create=true;
				}
				break;
			case Element.EVENT:
				
				break;
				
			case Element.STATE:
				State s=container.getStates().getMostRecent(_from);
				if (_symbolicValue.equalsIgnoreCase(s.getValue())){
					create=true;
					start=s.getStart();
					if (_relative.equalsIgnoreCase("Start")) 
						end=s.getStart()+_gap;
					else end=s.getEnd()+_gap;
				}
				break;
			case Element.TREND:
				Trend t=container.getTrends().getMostRecent(_from);
				if (_symbolicValue.equalsIgnoreCase(t.getValue())){
					create=true;
					start=t.getStart();
					if (_relative.equalsIgnoreCase("Start")) 
						end=t.getStart()+_gap;
					else end=t.getEnd()+_gap;
				}
				break;
			default:
				break;
		}
		if (create){
			Context context=container.getContexts().getMostRecent(_name);
			long contextEnd=context.getEnd();
			if (contextEnd<start){
				return new Context( _name, start, end);
			}
			else{
				if(contextEnd<end){
					context.setEnd(end);
				}
			}
		}
		return null;
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

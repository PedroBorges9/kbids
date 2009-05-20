/**
 * 
 */
package dt.processor.kbta.ontology.defs.context;

import android.os.Bundle;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.util.TimeInterval;

/**
 * @author
 */
public abstract class Induction{
	protected final String _contextName;

	protected final String _elementName;

	protected boolean _relativeToStart;

	protected long _gap;

	private boolean _setRelativeToAndGapCalled;

	public Induction(String elementName, String contextName){
		_elementName = elementName;
		_contextName = contextName;
		_setRelativeToAndGapCalled = false;
	}

	/**
	 * This method only exists due to the way inductions are parsed (the Ends tag is
	 * parsed separately from the tag inidicating the induction kind so we first must
	 * instantiate the object and only in the end set the relativeTo and gap fields). This
	 * method must not be called other than by the ontology loader during the
	 * initialization!
	 * 
	 * @param relativeToStart Whether the gap is relative to the start or end
	 * @param gap The time interval
	 * @return "this" (for chaining)
	 */
	public final Induction setRelativeToAndGap(boolean relativeToStart, long gap){
		if (_setRelativeToAndGapCalled){
			throw new UnsupportedOperationException("This method must not be "
					+ "called other than by the ontology "
					+ "loader during the initialization!");
		}
		_setRelativeToAndGapCalled = true;
		
		_relativeToStart = relativeToStart;
		_gap = gap;
		return this;
	}

	public abstract boolean induce(AllInstanceContainer container);

	protected boolean createContext(AllInstanceContainer container, long start, long end, Bundle extras){
		Context context = container.getContexts().getCurrentElement(_contextName);
		if (context != null){

//			android.util.Log.d("System.out", "Existing context: " + context);
			long contextEnd = context.getTimeInterval().getEndTime();
			if (contextEnd < start){
				container.addContext(new Context(_contextName, new TimeInterval(start,
						end), extras));
				return true;
			}else if (contextEnd < end){
				context.getTimeInterval().setEndTime(end);
//				android.util.Log.d("System.out", "Overlapping, new interval: " + context.getTimeInterval());
				context.addToInnerExtras(extras);
				return false;
			}
			return false;
		}else{
			//FIXME Before creating a new context we need to look
			// in the new contexts in case we created a context in this iteration
			// already and need to prolong it 
			container.addContext(new Context(_contextName, new TimeInterval(start, end), extras));
			return true;
		}
	}

	@Override
	public String toString(){
		String ans = "induction of " + _contextName + "\nfrom " + _elementName + " ";
		return ans + "\nthat ends at " + _gap + " miliseconds after the originator "
				+ _relativeToStart;

	}
	
	
	public abstract ElementDef getElementDef(Ontology ontology);
}

/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import dt.processor.kbta.ontology.defs.ElementDef;


/**
 * @author 
 *
 */
public class Element {
	protected final String _name;
	protected final long _start;
	protected long _end;
	
	public static final int PRIMITIVE=0;
	public static final int EVENT=1;
	public static final int CONTEXT=2;
	public static final int STATE=3;
	public static final int TREND=4;
	public static final int PATTERN=5;
	
	public Element( String name,long start, long end){
		  _name = name;
		  _start = start;
		  _end = end;
	}
	
	public String getName() {
		return _name;
	}
	
	
	
	

	public long getStart() {
		return _start;
	}

	public long getEnd() {
		return _end;
	}
	
	public long getDuration(){
		return _end - _start;
	}

	@Override
	public boolean equals(Object o) {
		// TODO compare by name and start and end
		return super.equals(o);
	}

	public void setEnd(long end){
		this._end = _end;
	}
	
	
}

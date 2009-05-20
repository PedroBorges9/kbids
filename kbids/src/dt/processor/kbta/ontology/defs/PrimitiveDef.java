/**
 * 
 */
package dt.processor.kbta.ontology.defs;

import java.util.Date;

import android.os.Bundle;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.instances.Primitive;

/**
 * @author
 */
public final class PrimitiveDef extends ElementDef{
	private final NumericRange _range;

	public PrimitiveDef(String name, NumericRange range){
		super(name);
		_range = range;
	}

	public void createPrimitive(Date end, double value, Bundle extras, AllInstanceContainer allInstances){
		if (_range.isInRange(value)){
			Primitive primitive = new Primitive(_name, value, end.getTime(), end.getTime(), extras);
			allInstances.addPrimitive(primitive);
		}
	}

	public void setInitiallyIsMonitored(Ontology ontology,boolean monitored){
		_isMonitored = monitored;
		_counter += (_isMonitored ? 1 : 0);
	}
	
	public void setIsMonitored(Ontology ontology,boolean monitored){
		_isMonitored = monitored;
		_counter += (_isMonitored ? 1 : (_counter > 0 ? -1 : 0));
	}
	
	@Override
	public String toString(){
		return "<Primitive name= " + _name + " " + _range +" isMonitored="+_isMonitored+" counter="+_counter+ " />";
	}
	
	
}

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

	public void createPrimitive(Date end, double value, Bundle extras,
		AllInstanceContainer allInstances){
		if (_range.isInRange(value)){
			Primitive primitive = new Primitive(_name, value, end.getTime(), end
					.getTime(), extras);
			allInstances.addPrimitive(primitive);
		}
	}
	
	@Override
	public void accept(Ontology ontology, ElementVisitor visitor){
		visitor.visit(this);
		// No one to traverse :)		
	}
	
	@Override
	public String toString(){
		return "Primitive: " + _name;
	}

}

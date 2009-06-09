/**
 * 
 */
package dt.processor.kbta.ontology.instances;

import java.util.List;
import java.util.Map;

import android.os.Bundle;

/**
 * @author
 */
public final class Primitive extends Element{
	private final double _value;

	public Primitive(String name, double value, long start, long end, Bundle extras){
		super(PRIMITIVE, name, start, end, extras);
		_value = value;
	}

	public double getValue(){
		return _value;
	}

	@Override
	public String toString(){
		return super.toString() + " value= " + _value;
	}

	@Override
	public int compareTo(Element another) {
		if (another.getClass().equals(Primitive.class)){
				double result=_value-((Primitive) another).getValue();
				if (result==0){
					return 0;
					
				}
				else if (result>0){
					return 1;
				}
				else if (result<0){
					return -1;
				}
				
		}
		return 0;
	}
	
	@Override
	public void toNetProtectElement(List<Map> elements){
		// Not sending primitives to NetProtect
	}
}

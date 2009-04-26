/**
 * 
 */
package dt.processor.kbta.ontology.defs;

/**
 * @author 
 *
 */
public class Induction {
	
	private String _type;
	private String _name;
	private String _symbolicValue;
	private NumericRange _numericValues;
	public Induction(String _type, String _name, String symbolicValue, NumericRange numericValues){
		this._type = _type;
		this._name = _name;
		_symbolicValue = symbolicValue;
		_numericValues = numericValues;
	}

}

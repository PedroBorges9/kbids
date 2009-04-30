package dt.processor.kbta.ontology.defs;

public class AbstractedFrom {
	private final String _type;//TODO change to int
	private final String _name;
	
	public AbstractedFrom(String type, String name) {
		_type = type;
		_name = name;
	}
	
	
	@Override
	public String toString() {
		return " type="+_type+" name="+_name;
	}

}

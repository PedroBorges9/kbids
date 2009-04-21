package dt.processor.kbta.threats;

public class ThreatAssessment {
	private final String _title;
	private final String _description;
	private final int _certainty;
	//TODO rest of the fields
	private final GeneratedFrom  _generatedFrom;
	
	public ThreatAssessment(String title, String description, int certainty, GeneratedFrom generatedFrom) {
		_title = title;
		_description = description;
		_certainty = certainty;
		_generatedFrom = generatedFrom;
	}
		
	public String getTitle() {
		return _title;
	}

	public String getDescription() {
		return _description;
	}


	public int getCertainty() {
		return _certainty;
	}


	public GeneratedFrom getGeneratedFrom() {
		return _generatedFrom;
	}




	@Override
	public String toString() {
		String st="Assessment\n";
		st+="title="+_title+" description="+_description+" certainty"+_certainty+"\n"+_generatedFrom.toString();
		return st;
	}
	
	
}

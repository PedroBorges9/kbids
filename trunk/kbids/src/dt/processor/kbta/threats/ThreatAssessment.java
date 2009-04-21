package dt.processor.kbta.threats;

public class ThreatAssessment {
	public final String title;
	public final String description;
	public final int certainty;
	//TODO rest of the fields
	
	public ThreatAssessment(String title, String description, int certainty) {
		this.title = title;
		this.description = description;
		this.certainty = certainty;
	}	
	
}

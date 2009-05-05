package dt.processor.kbta.threats;


public final class ThreatAssessment {
	private final String _title;
	private final String _description;
	private final int _certainty;
	private final GeneratedFrom _generatedFrom;

	public ThreatAssessment(String title, String description, int certainty,
			GeneratedFrom generatedFrom) {
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
		StringBuilder sb = new StringBuilder("Assessment\n");
		sb.append(_title).append("[").append(_certainty).append("]");
		sb.append("\nDescription: ").append(_description);
		sb.append("\nGenerated From: ").append(_generatedFrom).append("\n");
		return sb.toString();
	}

}

package dt.processor.kbta.threats;

import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;

public final class ThreatAssessment{
	private final String _title;

	private final String _description;

	private final int _baseCertainty;

	private final GeneratedFrom _generatedFrom;

	private boolean _monitored;

	public ThreatAssessment(String title, String description, int baseCertainty,
		boolean monitored, GeneratedFrom generatedFrom){
		_title = title;
		_description = description;
		_baseCertainty = baseCertainty;
		_monitored = monitored;
		_generatedFrom = generatedFrom;
	}

	public String getTitle(){
		return _title;
	}

	public String getDescription(){
		return _description;
	}

	public int getCertainty(Element e){
		long minDuration = _generatedFrom.getMinDuration();
		double actualDuration = e.getTimeInterval().getDuration();
		double percent = actualDuration / minDuration;

		return Math.min(100, (int)(_baseCertainty * percent));
	}

	public GeneratedFrom getGeneratedFrom(){
		return _generatedFrom;
	}
	
	public void setInitiallyMonitoredThreat(Ontology ontology){
		ElementDef elementDef=_generatedFrom.getElementDef(ontology);
		elementDef.setInitiallyIsMonitored(ontology,_monitored);
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("\nAssessment\n");
		sb.append(_title).append(" [BaseCert").append(_baseCertainty).append("]");
		sb.append("\nDescription: ").append(_description);
		sb.append("\nmonitored= ").append(_monitored);
		sb.append("\nGenerated From: ").append(_generatedFrom);
		return sb.toString();
	}

	public String toString(Element e){
		StringBuilder sb = new StringBuilder("\nAssessment\n");
		sb.append(_title).append(" [").append(getCertainty(e)).append("]");
		sb.append("\nDescription: ").append(_description);
		sb.append("\nmonitored= ").append(_monitored);
		sb.append("\nGenerated From: ").append(_generatedFrom);
		return sb.toString();
	}

	
}

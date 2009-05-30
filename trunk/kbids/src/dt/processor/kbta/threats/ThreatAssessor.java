package dt.processor.kbta.threats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dt.processor.kbta.Env;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.ElementDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.settings.SettingsScreen;
import dt.processor.kbta.util.Pair;

public class ThreatAssessor{
	private final TreeMap<String, ThreatAssessment> _assessments;

	private final String _threatsName;

	private final String _version;

	ThreatAssessor(TreeMap<String, ThreatAssessment> assessments,
		String threatsName, String version){
		_assessments = assessments;
		_threatsName = threatsName;
		_version = version;
	}

	public Collection<Pair<ThreatAssessment, Element>> assess(
		AllInstanceContainer allInstances){
		Collection<Pair<ThreatAssessment, Element>> assessments = new ArrayList<Pair<ThreatAssessment, Element>>();

		for (ThreatAssessment ta : _assessments.values()){
			if (!ta.isMonitored()){
				continue;
			}
			GeneratedFrom gf = ta.getGeneratedFrom();
			Element element = gf.locateMatchingElement(allInstances);
			if (element != null){
				assessments.add(new Pair<ThreatAssessment, Element>(ta, element));
			}
		}

		return assessments;
	}

	public void setInitiallyMonitoredThreats(Ontology ontology){
		Collection<String> discardedTAs = new ArrayList<String>();
		SharedPreferences.Editor spe = Env.getSharedPreferences().edit();
		for (ThreatAssessment ta : _assessments.values()){
			if (!ta.setInitiallyMonitoredThreat(ontology, spe)){
				discardedTAs.add(ta.getTitle());
			}
		}
		for (String title : discardedTAs){
			_assessments.remove(title);
		}
		spe.commit();
	}
	
	@Override
	public String toString(){
		String st = "";
		for (ThreatAssessment t : _assessments.values()){
			st += t + "\n";
		}

		return st;
	}

	public String getName(){
		return _threatsName;
	}

	public String getVersion(){
		return _version;
	}

	public Collection<ThreatAssessment> getThreatAssessments(){
		return _assessments.values();
	}
}

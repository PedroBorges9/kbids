package dt.processor.kbta.settings;

import java.io.File;
import java.util.Collection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;
import dt.processor.kbta.Env;
import dt.processor.kbta.KBTAProcessorService;
import dt.processor.kbta.Env.LoadingCallback;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.threats.ThreatAssessment;
import dt.processor.kbta.threats.ThreatAssessor;

public class SettingsScreen extends PreferenceActivity{

	private Model _threats;

	private Model _ontology;

	private PreferenceCategory _monitoredThreats;

	private boolean _isServiceRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		setPreferenceScreen(root);
		
		_isServiceRunning = KBTAProcessorService.isRunning();
		_monitoredThreats = new PreferenceCategory(this);
		_monitoredThreats.setTitle("Monitored Threats");

		_ontology = new OntologyModel(this, _monitoredThreats, Model.getOntologyModelFile(this), "Ontology",
				"Ontology", _isServiceRunning);

		_threats = new ThreatsModel(this, _monitoredThreats, Model.getThreatsModelFile(this), "Threats",
				"Assessments", _isServiceRunning);

		_ontology.initPrefs(root);
		_threats.initPrefs(root);

		root.addPreference(_monitoredThreats);

		// Load the ontology and threat assessments
		// Starting the initialization process
		Env.initialize(this, new InitializationCallback(), false);
	}

	@Override
	protected void onPause(){
		// If the models are incompatible or not loaded properly
		// we revert to the default models
		if (!areModelsCompatibile()){
			Toast.makeText(this, "Reverting to default models...", Toast.LENGTH_SHORT)
					.show();
			
			// Rollbacl, must not fail
			Model.copyDefaultModelFile(this, Model.getOntologyModelFile(this));
			Model.copyDefaultModelFile(this, Model.getThreatsModelFile(this)); 
			Env.initialize(this, null, true);
		}

		super.onPause();
	}

	boolean areModelsCompatibile(){
		String ontologyName = _ontology.getNameOfCurrentlyLoadedModel();
		String threatsName = _threats.getNameOfCurrentlyLoadedModel();

		return ontologyName != null && threatsName != null
				&& ontologyName.equalsIgnoreCase(threatsName);
	}

	void initMonitoredThreatsPrefs(){
		SharedPreferences sp = Env.getSharedPreferences();
		ThreatAssessor ta = Env.getThreatAssessor();
		Collection<ThreatAssessment> threats = ta.getThreatAssessments();
		for (final ThreatAssessment threat : threats){
			CheckBoxPreference cbp = new CheckBoxPreference(SettingsScreen.this);
			String title = threat.getTitle();
			cbp.setTitle(title);
			cbp.setSummary(threat.getDescription());
			cbp.setKey(title);
			cbp.setPersistent(true);
			cbp.setChecked(sp.getBoolean(title, false));
			if (_isServiceRunning){
				cbp.setEnabled(false);
			}else{
				cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

					@Override
					public boolean onPreferenceChange(Preference preference,
						Object isMonitored){
						threat.setMonitoredThreat(Env.getOntology(), (Boolean)isMonitored);
						return true;
					}

				});				
			}
			_monitoredThreats.addPreference(cbp);
		}
	}

	private final class InitializationCallback implements Env.LoadingCallback{
		@Override
		public void onSuccess(){
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					_ontology.refreshDetails();
					_threats.refreshDetails();

					initMonitoredThreatsPrefs();
				}
			});
		}

		@Override
		public void onFailure(){
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					Toast.makeText(SettingsScreen.this,
						"Unable to initialize Ontology and Threats", Toast.LENGTH_SHORT)
							.show();
					finish();
				}
			});
		}
	}

	private final class OntologyModel extends Model{
		private OntologyModel(SettingsScreen settingsScreen, PreferenceCategory monitoredThreats, File modelFile,
			String modelName, String xmlRootTag, boolean isServiceRunning){
			super(settingsScreen, monitoredThreats, modelFile, modelName, xmlRootTag, isServiceRunning);
		}

		@Override
		public String getNameOfCurrentlyLoadedModel(){
			Ontology ontology = Env.getOntology();
			return (ontology == null) ? null : ontology.getName();
		}

		@Override
		public String getNameAndVersionOfCurrentlyLoadedModel(){
			Ontology ontology = Env.getOntology();
			return (ontology == null) ? null : (ontology.getName() + " V." + ontology
					.getVersion());
		}

		@Override
		public void loadModel(Context context, LoadingCallback callback){
			Env.loadOntology(context, callback);
		}
	}

	private final class ThreatsModel extends Model{
		private ThreatsModel(SettingsScreen settingsScreen, PreferenceCategory monitoredThreats, File modelFile,
			String modelName, String xmlRootTag, boolean isServiceRunning){
			super(settingsScreen, monitoredThreats, modelFile, modelName, xmlRootTag, isServiceRunning);
		}

		@Override
		public String getNameOfCurrentlyLoadedModel(){
			ThreatAssessor threats = Env.getThreatAssessor();
			return (threats == null) ? null : threats.getName();
		}

		@Override
		public String getNameAndVersionOfCurrentlyLoadedModel(){
			ThreatAssessor threats = Env.getThreatAssessor();
			return (threats == null) ? null : (threats.getName() + " V." + threats
					.getVersion());
		}

		@Override
		public void loadModel(Context context, LoadingCallback callback){
			Env.loadThreats(context, callback);
		}
	}
}

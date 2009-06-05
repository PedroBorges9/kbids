package dt.processor.kbta;

import static dt.processor.kbta.Env.TAG;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.OntologyLoader;
import dt.processor.kbta.threats.ThreatAssessmentLoader;
import dt.processor.kbta.threats.ThreatAssessor;

public class Env{
	public static final String TAG = "KBTA";

	private static Ontology _ontology;

	private static ThreatAssessor _threatAssessor;

	private static SharedPreferences _sp;

	public static void initialize(final Context context, final LoadingCallback callback,
		boolean forceReload){
		_sp = PreferenceManager.getDefaultSharedPreferences(context);

		// Checking whether the ontology and threats were already initialized
		if (!forceReload && _ontology != null && _threatAssessor != null){
			if (callback != null){
				callback.onSuccess();
			}
			return;
		}

		// Performing the loading in separate threats and syncing them on sync
		final Object sync = new Object();
		final Thread threatsThread = new Thread(new Runnable(){

			@Override
			public void run(){
				ThreatAssessmentLoader threatAssessmentLoader = new ThreatAssessmentLoader();
				Log.i(TAG, "Started loading the threat assessments");
				long start = System.currentTimeMillis();
				_threatAssessor = threatAssessmentLoader.loadThreatAssessments(context);

				long end = System.currentTimeMillis();
				if (_threatAssessor == null){
					Log.e(TAG, "Failed loading the threat assessments");
				}else{
					Log.i(TAG, "Loaded the threat assessments '"
							+ _threatAssessor.getName() + "' in " + (end - start)
							+ " millis");
				}

				synchronized (sync){
					// Waiting on sync only if the ontology hasn't finished yet.
					// Checking that the thread hasn't been interrupted to make sure
					// that the ontology thread has finished and failed (in which case
					// _ontology will remain null)
					if (_ontology == null && !Thread.currentThread().isInterrupted()){
						try{
							sync.wait();
						}catch(InterruptedException e){
							Log.e(TAG, Thread.currentThread().getName()
									+ " was interrupted!!!");
						}
					}
				}

				if (_threatAssessor == null || _ontology == null){
					if (callback != null){
						callback.onFailure(null);
					}
					return;
				}
				try{
					_threatAssessor.setInitiallyMonitoredThreats(_ontology);					
				}catch(Exception e){
					Log.e(TAG, "Failed setting initially monitored threats", e);
					if (callback != null){
						callback.onFailure(e);
					}
					return;					
				}

				// Notifying that the ontology and threat assessments have been loaded
				if (callback != null){
					callback.onSuccess();
				}
			}
		}, "Threat Assessment Loader Thread");

		final Thread ontologyThread = new Thread(new Runnable(){

			@Override
			public void run(){
				OntologyLoader ontologyLoader = new OntologyLoader();
				Log.i(TAG, "Started loading the ontology");
				long start = System.currentTimeMillis();
				Ontology ontology = ontologyLoader.loadOntology(context);
				long end = System.currentTimeMillis();
				synchronized (sync){
					_ontology = ontology;
					sync.notify();

					// Flagging the threats thread that the ontology
					// loading has failed and it shouldn't wait on sync
					if (ontology == null){
						threatsThread.interrupt();
					}
				}
				if (ontology == null){
					Log.e(TAG, "Failed loading the ontology");
				}else{
					Log.i(TAG, "Loaded the ontology '" + ontology.getName() + "' in "
							+ (end - start) + " millis");
				}
			}
		}, "Ontology Loader Thread");

		ontologyThread.start();
		threatsThread.start();
	}

	public static void loadOntology(final Context context, final LoadingCallback callback){
		new Thread(new Runnable(){

			@Override
			public void run(){
				OntologyLoader ontologyLoader = new OntologyLoader();
				Log.i(TAG, "Started loading the ontology");
				long start = System.currentTimeMillis();
				_ontology = ontologyLoader.loadOntology(context);
				long end = System.currentTimeMillis();

				if (_ontology == null || _threatAssessor == null){
					Log.e(TAG, "Failed loading the ontology");
					if (callback != null){
						callback.onFailure(null);
					}
				}else{
					Log.i(TAG, "Loaded the ontology '" + _ontology.getName() + "' in "
							+ (end - start) + " millis");
					if (callback != null){
						callback.onSuccess();
					}
				}
			}
		}, "Ontology Loader Thread").start();
	}

	public static void loadThreats(final Context context, final LoadingCallback callback){
		new Thread(new Runnable(){

			@Override
			public void run(){
				ThreatAssessmentLoader threatAssessmentLoader = new ThreatAssessmentLoader();
				Log.i(TAG, "Started loading the threat assessments");
				long start = System.currentTimeMillis();
				_threatAssessor = threatAssessmentLoader.loadThreatAssessments(context);
				long end = System.currentTimeMillis();

				if (_threatAssessor == null || _ontology == null){
					Log.e(TAG, "Failed loading the threat assessments");
					if (callback != null){
						callback.onFailure(null);
					}
				}else{
					// Resetting the monitored state of all elements in the ontology to
					// false
					_ontology.setAllElementsInitiallyUnmonitored();

					// Resetting the monitored state of all threats in persistent storage
					// to false
					SharedPreferences.Editor spe = Env.getSharedPreferences().edit();
					spe.clear();
					spe.commit();

					Log.i(TAG, "Loaded the threat assessments '"
							+ _threatAssessor.getName() + "' in " + (end - start)
							+ " millis");

					// Notifying that the threat assessments have been loaded
					if (callback != null){
						callback.onSuccess();
					}
				}

			}
		}, "Threat Assessment Loader Thread").start();
	}

	public static Ontology getOntology(){
		return _ontology;
	}

	public static ThreatAssessor getThreatAssessor(){
		return _threatAssessor;
	}

	public static SharedPreferences getSharedPreferences(){
		return _sp;
	}

	public interface LoadingCallback{
		public void onSuccess();

		public void onFailure(Throwable t);
	}
}

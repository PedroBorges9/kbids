package dt.processor.kbta;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import dt.agent.twu.TWU;
import dt.fe.MonitoredData;
import dt.processor.Processor;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.OntologyLoader;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.abstractions.StateDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.ThreatAssessment;
import dt.processor.kbta.threats.ThreatAssessmentLoader;
import dt.processor.kbta.threats.ThreatAssessor;
import dt.processor.kbta.util.Pair;

public final class KBTAProcessorService extends Service implements ServiceConnection{
	public static final String TAG = "KBTA";

	public static final boolean DEBUG = false;

	private TWU _twu;

	private Ontology _ontology;

	private AllInstanceContainer _allInstances;

	private ThreatAssessor _threatAssessor;

	private int _iteration;

	private long _elementTimeout;

	@Override
	public void onCreate(){
		super.onCreate();
		_allInstances = new AllInstanceContainer();
		_iteration = 0;
		new Thread(new Runnable(){

			@Override
			public void run(){
				OntologyLoader ontologyLoader = new OntologyLoader();
				_ontology = ontologyLoader.loadOntology(KBTAProcessorService.this);
				_elementTimeout = _ontology.getElementTimeout();
				Log.i(TAG, "Finished loading the ontology: " + _ontology.getOntologyName());
				
			}
		}, "Ontology Loader Thread").start();

		new Thread(new Runnable(){

			@Override
			public void run(){
				ThreatAssessmentLoader threatAssessmentLoader = new ThreatAssessmentLoader();
				_threatAssessor = threatAssessmentLoader
						.loadThreatAssessments(KBTAProcessorService.this);
				Log.i(TAG, "Finished loading the threat assessments");
			}
		}, "Threat Assessment Loader Thread").start();

		// Connecting to the TWU so we can send threat assessments
		bindService(new Intent("dt.agent.action.BIND_SERVICE")
				.addCategory("dt.agent.category.TWU_SERVICE"), this, BIND_AUTO_CREATE);
	}

	/**
	 * Here you should probably unbind from the TWU service
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();

		if (_twu != null){ // Unbinding from the TWU if connected
			unbindService(this);
			_twu = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent){
		return new Processor.Stub(){

			@Override
			public void receiveMonitoredData(List<MonitoredData> features)
					throws RemoteException{
				try{
					compute(features);

					if (_threatAssessor == null){
						return;
					}
					Collection<Pair<ThreatAssessment, Element>> threats = _threatAssessor
							.assess(_allInstances);
					if (!threats.isEmpty()){
						for (Pair<ThreatAssessment, Element> p : threats){
							Log.d(TAG, p.first.toString(p.second));
							Log.d(TAG, "Element: " + p.second.toString());
						}
					}
					if (_twu != null){
						for (Pair<ThreatAssessment, Element> p : threats){
							ThreatAssessment ta = p.first;
							Element element = p.second;
							_twu.receiveThreatAssessment("dt.processor.kbta", ta
									.getTitle(), ta.getDescription(), ta
									.getCertainty(element), element.getExtras());
						}
					}
				}catch(Throwable t){
					System.err.println("This should've been caught sooner!!!");
					t.printStackTrace();
				}
			}

			@Override
			public void stoppedMonitoring() throws RemoteException{
				// Called by the agent when it stops running
			}
		};
	}

	void compute(List<MonitoredData> features){
		if (_ontology == null){
			return;
		}
		++_iteration;
		createPrimitivesAndEvents(features);
		if (DEBUG)
			System.out.println("\n--------------- Global iteration #" + _iteration
					+ "---------------\n");
		boolean cont = false;
		int i = 1;
		do{
			if (DEBUG)
				System.out.println("---- Inner iteration #" + i++ + "----");
			_allInstances.getContexts().shiftBack();
			createContexts();
			createAbstractions();
			cont = _allInstances.hasNew();
		}while (cont);

		createPatterns();
		_allInstances.shiftBackAll();
		_allInstances.discardElementsNotWithinRange(_elementTimeout);
	}

	private void createContexts(){
		ContextDef[] contextDefs = _ontology.getContextDefiners();
		for (ContextDef cd : contextDefs){
			cd.createContext(_allInstances, _iteration);
		}
		if (DEBUG)
			System.out.println("** Contexts: **\n" + _allInstances.getContexts());
	}

	private void createAbstractions(){
		_allInstances.getStates().shiftBack();
		createStates();
		_allInstances.getTrends().shiftBack();
		createTrends();
	}

	private void createStates(){
		StateDef[] stateDefs = _ontology.getStateDefiners();
		for (StateDef sd : stateDefs){
			sd.createState(_allInstances, _iteration);
		}
		if (DEBUG)
			System.out.println("** States: **\n" + _allInstances.getStates());
	}

	private void createTrends(){
		// TODO create trends
	}

	private void createPatterns(){
		// TODO create patterns
	}

	private void createPrimitivesAndEvents(List<MonitoredData> features){
		for (MonitoredData md : features){
			// Extracting the properties of the feature
			String name = md.getName();
			Date start = md.getStartTime();
			Date end = md.getEndTime();
			Double value = md.getValue();
			Bundle extras = md.getExtras();
			if (name == null || start == null || end == null || value == null){
				continue;
			}

			// Matching feature to a primitive
			PrimitiveDef pd = _ontology.getPrimitiveDef(name);
			if (pd != null){
				pd.createPrimitive(end, value, extras, _allInstances);
			}

			// Matching feature to an event
			EventDef ed = _ontology.getEventDef(name);
			if (ed != null){
				ed.createEvents(extras, _allInstances);
			}
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service){
		if (service == null){
			Log.w(TAG, "The TWU returned a null binder");
		}else{
			// Casting the service object to "TWU"
			_twu = TWU.Stub.asInterface(service);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name){
		// Nothing to do but wait until the agent's process revives
		_twu = null;
	}
}

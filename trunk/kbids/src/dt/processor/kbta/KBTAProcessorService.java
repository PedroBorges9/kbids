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
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.defs.Patterns.LinearPatternDef;
import dt.processor.kbta.ontology.instances.Context;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.ThreatAssessment;
import dt.processor.kbta.threats.ThreatAssessmentLoader;
import dt.processor.kbta.threats.ThreatAssessor;
import dt.processor.kbta.util.Pair;

public final class KBTAProcessorService extends Service implements ServiceConnection{
	private static boolean _isRunning;

	public static final boolean DEBUG = true;

	private TWU _twu;

	private Ontology _ontology;

	private AllInstanceContainer _allInstances;

	private ThreatAssessor _threatAssessor;

	private int _iteration;

	@Override
	public void onCreate(){
		super.onCreate();

		setIsRunning(true);

		_allInstances = new AllInstanceContainer();
		_iteration = 0;

		// Connecting to the TWU so we can send threat assessments
		System.out.println(bindService(new Intent("dt.agent.action.BIND_SERVICE")
				.addCategory("dt.agent.category.TWU_SERVICE"), this, BIND_AUTO_CREATE));

		Env.initialize(this, new Env.LoadingCallback(){

			@Override
			public void onFailure(){
			}

			@Override
			public void onSuccess(){
				_threatAssessor = Env.getThreatAssessor();
				_ontology = Env.getOntology();
			}

		}, false);
	}

	/**
	 * Here you should probably unbind from the TWU service
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();

		setIsRunning(false);
		if (_twu != null){ // Unbinding from the TWU if connected
			unbindService(this);
			_twu = null;
		}
	}

	private static synchronized void setIsRunning(boolean isRunning){
		_isRunning = isRunning;
	}

	public static synchronized boolean isRunning(){
		return _isRunning;
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
							Log.d(Env.TAG, p.first.toString(p.second));
							Log.d(Env.TAG, "Element: " + p.second.toString());
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
		if (DEBUG)
			System.out.println("\n--------------- Global iteration #" + _iteration
					+ "---------------\n");
		createPrimitivesAndEvents(features);
		if (DEBUG)
			System.out.println("** Events: **\n" + _allInstances.getEvents());

		if (DEBUG)
			System.out.println("** Primitives: **\n" + _allInstances.getPrimitives());
		boolean cont = false;
		int i = 1;
		do{
			// destroyContexts();
			if (DEBUG)
				System.out.println("---- Inner iteration #" + i++ + "----");
			_allInstances.getContexts().shiftBack();
			createContexts();
			createAbstractions();
			cont = _allInstances.hasNew();
		}while (cont);

		createPatterns();
		_allInstances.shiftBackAll();
		// TODO See if ontology needs to be saved here or only in the env
		_allInstances.discardElementsNotWithinRange(_ontology.getElementTimeout());
	}

	private void destroyContexts(){
		ContextDef[] contextDefs = _ontology.getContextDefiners();
		for (ContextDef cd : contextDefs){
			cd.destroyContext(_allInstances, _iteration);

		}
		if (DEBUG)
			System.out.println("** Contexts: **\n" + _allInstances.getContexts());

	}

	private void createContexts(){
		ContextDef[] contextDefs = _ontology.getContextDefiners();
		for (ContextDef cd : contextDefs){
			// TODO if isMonitored
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
		TrendDef[] trendDefs = _ontology.getTrendDefiners();
		for (TrendDef td : trendDefs){
			td.createTrend(_allInstances, _iteration);
		}
		if (DEBUG)
			System.out.println("** Trends: **\n" + _allInstances.getTrends());
	}

	private void createPatterns(){
		LinearPatternDef[] lpd = _ontology.getLinearPatternDefs();
		for (LinearPatternDef lp : lpd){
			lp.createPattern(_allInstances);

		}
		if (DEBUG)
			System.out.println("** Patterns: **\n" + _allInstances.getPatterns());
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
			if (pd != null /* TODO and isMonitored */){
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
			Log.w(Env.TAG, "The TWU returned a null binder");
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

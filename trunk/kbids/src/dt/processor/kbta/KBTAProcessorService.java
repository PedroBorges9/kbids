package dt.processor.kbta;

import static dt.processor.kbta.Env.TAG;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import dt.agent.twu.TWU;
import dt.fe.MonitoredData;
import dt.processor.Processor;
import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.defs.patterns.LinearPatternDef;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.ThreatAssessment;
import dt.processor.kbta.threats.ThreatAssessor;
import dt.processor.kbta.util.Pair;

public final class KBTAProcessorService extends Service implements ServiceConnection{
	private static final String AGENT_PACKAGE_NAME = "dt.agent";

	public static final boolean DEBUG = false;

	private static boolean _isRunning;

	private TWU _twu;

	private Ontology _ontology;

	private AllInstanceContainer _allInstances;

	private ThreatAssessor _threatAssessor;

	private NetProtectConnection _npc;

	private int _iteration;

	@Override
	public void onCreate(){
		super.onCreate();

		setIsRunning(true);

		try{
			_npc = new NetProtectConnection(this, AGENT_PACKAGE_NAME);
		}catch(NameNotFoundException e){
			Log.e(TAG, "Unable to obtain Agent context with package name: "
					+ AGENT_PACKAGE_NAME);
			e.printStackTrace();
		}
		_allInstances = new AllInstanceContainer();
		_iteration = 0;

		// Connecting to the TWU so we can send threat assessments
		bindService(new Intent("dt.agent.action.BIND_SERVICE")
				.addCategory("dt.agent.category.TWU_SERVICE"), this, BIND_AUTO_CREATE);

		Env.initialize(this, new Env.LoadingCallback(){

			@Override
			public void onFailure(Throwable t){
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
		setIsRunning(false);
		if (_twu != null){ // Unbinding from the TWU if connected
			unbindService(this);
			_twu = null;
		}

		super.onDestroy();
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
							Log.d(TAG, p.first.toString(p.second));
							Log.d(TAG, "Element: " + p.second.toString());
						}
					}
					try{
						if (_npc != null){
							for (Pair<ThreatAssessment, Element> p : threats){
								_npc.sendRelatedElementsOf(p.second);
							}
						}
					}catch(Exception e){
						Log.e(TAG, "Unable to send monitored elements to NetProtect", e);
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
					Log.e(TAG, "This should've been caught sooner!!!", t);
				}

			}

			@Override
			public void stoppedMonitoring() throws RemoteException{
				_ontology.resetLastCreated();
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
		// if (DEBUG)
		// System.out.println("** Events: **\n" + _allInstances.getEvents());

		// if (DEBUG)
		// System.out.println("** Primitives: **\n" + _allInstances.getPrimitives());
		int i = 1;
		do{
			if (DEBUG)
				System.out.println("---- Inner iteration #" + i++ + "----");
			// destroyContexts();
			_allInstances.getContexts().shiftBack();
			createContexts();
			_allInstances.getStates().shiftBack();
			createStates();
			_allInstances.getTrends().shiftBack();
			createTrends();
		}while (_allInstances.hasNew());

		createPatterns();
		_allInstances.shiftBackAll();
		_allInstances.discardElementsNotWithinRange(_ontology.getElementTimeout());
	}

	private void destroyContexts(){
		ContextDef[] contextDefs = _ontology.getContextDefs();
		for (ContextDef cd : contextDefs){
			cd.destroyContext(_allInstances, _iteration);

		}
		// if (DEBUG)
		// System.out.println("** Contexts: **\n" + _allInstances.getContexts());

	}

	private void createContexts(){
		ContextDef[] contextDefs = _ontology.getContextDefs();
		for (ContextDef cd : contextDefs){
			if (cd.isMonitored()){
				cd.createContext(_allInstances, _iteration);
			}
		}
		// if (DEBUG)
		// System.out.println("** Contexts: **\n" + _allInstances.getContexts());
	}

	private void createStates(){
		StateDef[] stateDefs = _ontology.getStateDefs();
		for (StateDef sd : stateDefs){
			if (sd.isMonitored()){
				sd.createState(_allInstances, _iteration);
			}
		}
		// if (DEBUG)
		// System.out.println("** States: **\n" + _allInstances.getStates());
	}

	private void createTrends(){
		TrendDef[] trendDefs = _ontology.getTrendDefs();
		for (TrendDef td : trendDefs){
			if (td.isMonitored()){
				td.createTrend(_allInstances, _iteration);
			}
		}
		// if (DEBUG)
		// System.out.println("** Trends: **\n" + _allInstances.getTrends());
	}

	private void createPatterns(){
		LinearPatternDef[] linearPatternDefs = _ontology.getLinearPatternDefs();
		for (LinearPatternDef lpd : linearPatternDefs){
			if (lpd.isMonitored()){
				lpd.createPattern(_allInstances);
			}

		}
		if (DEBUG)
			System.out.println("** Patterns: **\n" + _allInstances.getLinearPatterns());
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
			if (pd != null && pd.isMonitored()){
				pd.createPrimitive(end, value, extras, _allInstances);
			}

			// Matching feature to an event
			EventDef ed = _ontology.getEventDef(name);
			if (ed != null && ed.isMonitored()){
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

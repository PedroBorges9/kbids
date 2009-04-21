package dt.processor.kbta;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.*;
import android.R.string;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.XmlResourceParser;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import dt.fe.MonitoredData;
import dt.processor.Processor;
import dt.processor.kbta.ontology.*;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.instances.Event;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.threats.*;
import dt.agent.twu.TWU;

public class KBTAProcessorService extends Service implements ServiceConnection {
	public static final String TAG = "KBTAProcessor";

	private TWU _twu;
	private Ontology _ontology;
	private AllInstanceContainer _allInstances;
	private ThreatAssessor _threatAssessor;

	@Override
	public void onCreate() {
		super.onCreate();
		_allInstances = new AllInstanceContainer();

		new Thread(new Runnable() {

			@Override
			public void run() {
				OntologyLoader ontologyLoader = new OntologyLoader();
				_ontology = ontologyLoader
						.loadOntology(KBTAProcessorService.this);
			}
		}, "Ontology Loader Thread").start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				ThreatAssessmentLoader threatAssessmentLoader = new ThreatAssessmentLoader();
				_threatAssessor = threatAssessmentLoader
						.loadThreatAssessments(KBTAProcessorService.this);
			}
		}, "Threat Assessment Loader Thread").start();
		
		// This is the constructor, do any initialization you require here
		// like reading the ontology, parsing stuff and so on...

		// Connecting to the TWU so we can send threat assessments
		/*
		 * bindService(new Intent("dt.agent.action.BIND_SERVICE")
		 * .addCategory("dt.agent.category.TWU_SERVICE"), this,
		 * BIND_AUTO_CREATE);
		 */
	}

	/**
	 * Here you should probably unbind from the TWU service
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		// Destructor, clean up... shut down computation units, etc.

		if (_twu != null) { // Unbinding from the TWU if connected
			unbindService(this);
			_twu = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new Processor.Stub() {

			@Override
			public void receiveMonitoredData(List<MonitoredData> features)
					throws RemoteException {
				try {
					process(features);

					if (_threatAssessor == null) {
						return;
					}

					Collection<ThreatAssessment> threats = _threatAssessor
							.assess(_allInstances);

					if (_twu != null) {
						// Sending the processor's name, threat title, threat
						// description
						// and the certainty of the processor that the threat
						// exists
						// (0 - 100)%
						for (ThreatAssessment ta : threats){
							_twu.receiveThreatAssessment("dt.processor.kbta",
									ta.title, ta.description, ta.certainty);
						}
					}
				} catch (Throwable t) {
					System.err.println("This should've been caught sooner!!!");
					t.printStackTrace();
				}
			}

			@Override
			public void stoppedMonitoring() throws RemoteException {
				// Called by the agent when he stops running
				// do whatever you want here
			}
		};
	}

	void process(List<MonitoredData> features) {
		if (_ontology == null) {
			return;
		}

		featuresToElements(features);

		boolean cont = false;

		do {
			createContexts();
			createAbstractions();
			// cont = whether something new happened
		} while (cont);

		createPatterns();
	}

	private void createPatterns() {
		// TODO Auto-generated method stub

	}

	private void createAbstractions() {
		// TODO Auto-generated method stub

	}

	private void createContexts() {
		// TODO Auto-generated method stub

	}

	private void featuresToElements(List<MonitoredData> features) {
		for (MonitoredData m : features) {
			// Extracting the properties of the feature
			String name = m.getName();
			Date start = m.getStartTime();
			Date end = m.getEndTime();
			Double value = valueToDouble(m);
			if (name == null || start == null || end == null || value == null) {
				continue;
			}

			// Matching feature to a primitive
			PrimitiveDef pd = _ontology.getPrimitiveDef(name);
			if (pd != null) {
				Primitive primitive = pd.definePrimitive(start, end, value);
				_allInstances.addPrimitive(primitive);
				System.out.println(primitive.toString()); // TODO Remove
			}

			EventDef ed = _ontology.getEventDef(name);
			if (ed != null) {
				Collection<Event> events = ed.defineEvents(m);
				if (events != null) {
					for (Event event : events) {
						_allInstances.addEvent(event);
						System.out.println(event); // TODO Remove
					}
				}
			}
		}
	}

	public Double valueToDouble(MonitoredData md) {
		Object value = md.getValue();

		if (value instanceof Integer) {
			return ((Integer) value).doubleValue();
		} else if (value instanceof Long) {
			return ((Long) value).doubleValue();
		} else if (value instanceof Double) {
			return ((Double) value).doubleValue();
		}
		return null;

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if (service == null) {
			Log.w(TAG, "The TWU returned a null binder");
		} else {
			// Casting the service object to "TWU"
			_twu = TWU.Stub.asInterface(service);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// Nothing to do but wait until the agent's process revives
		_twu = null;
	}
}

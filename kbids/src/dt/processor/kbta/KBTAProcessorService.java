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
import dt.processor.kbta.ontology.Event;
import dt.processor.kbta.ontology.EventDef;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.OntologyLoader;
import dt.processor.kbta.ontology.Primitive;
import dt.processor.kbta.ontology.PrimitiveDef;
import dt.agent.twu.TWU;

public class KBTAProcessorService extends Service implements ServiceConnection {
	public static final String TAG = "KBTAProcessor";

	private TWU _twu;
	private Ontology _ontology;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("creating kbta");

		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("loading ");
				OntologyLoader ontologyLoader = new OntologyLoader();
				_ontology = ontologyLoader
						.loadOntology(KBTAProcessorService.this);
			}
		}).start();

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
		System.out.println("bind");
		return new Processor.Stub() {

			@Override
			public void receiveMonitoredData(List<MonitoredData> features)
					throws RemoteException {
				try {
					Primitive p = null;
					for (MonitoredData m : features) {
						// Extracting the properties of the feature
						String name = m.getName();
						Date start = m.getStartTime();
						Date end = m.getEndTime();
						Double value = valueToDouble(m);
						if (name == null || start == null || end == null
								|| value == null) {
							continue;
						}
						
						// Matching feature to a primitive
						PrimitiveDef pd = _ontology.getPrimitiveDef(name);
						if (pd != null) {
							p = pd.definePrimitive(start, end, value);
							System.out.println(p.toString());
							//TODO do something with the primitive
						}
						
						EventDef ed=_ontology.getEventDef(name);
						if (ed != null) {
							Collection<Event> events = ed.defineEvents(m);
							if (events != null){
								for (Event event : events){
									//TODO do something with the events
									System.out.println(event);
								}
							}
						}
					}
					// This is what is called by the agent when he sends the
					// data
					// A MonitoredData object is a feature (name, start time,
					// end
					// time, value)

					// Here you should do your computation
					// for (MonitoredData feature : features){
					// System.out.println(feature);
					// }
					// Once the computation is finished, we send the threat
					// assessment to the TWU
					if (_twu != null) {
						// Sending the processor's name, threat title, threat
						// description
						// and the certainty of the processor that the threat
						// exists
						// (0 - 100)%
						_twu.receiveThreatAssessment("dt.processor.kbta",
								"Threat title", "Threat description", 100);

					}
				} catch (Throwable t) {
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

package dt.processor.kbta;

import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.XmlResourceParser;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import dt.fe.MonitoredData;
import dt.processor.Processor;
import dt.agent.twu.TWU;

public class KBTAProcessorService extends Service implements ServiceConnection{
	public static final String TAG = "KBTAProcessor";
	
	private TWU _twu;

	@Override
	public void onCreate(){
		super.onCreate();
		XmlResourceParser ontology = this.getResources().getXml(R.xml.ontology);
		//ontology.

		// This is the constructor, do any initialization you require here
		// like reading the ontology, parsing stuff and so on...
		
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

		// Destructor, clean up... shut down computation units, etc.
		
		if (_twu != null){ // Unbinding from the TWU if connected
			unbindService(this);
			_twu = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent){
		return new Processor.Stub(){

			@Override
			public void receiveMonitoredData(List<MonitoredData> monitoredData)
					throws RemoteException{
				// This is what is called by the agent when he sends the data
				// A MonitoredData object is a feature (name, start time, end time, value)
				
				// Here you should do your computation
				
				// Once the computation is finished, we send the threat assessment to the TWU
				if (_twu != null){			
						// Sending the processor's name, threat title, threat description
						// and the certainty of the processor that the threat exists (0 - 100)%
						_twu.receiveThreatAssessment("dt.processor.kbta", "Threat title",
							"Threat description", 100); 
					
				}
			}

			@Override
			public void stoppedMonitoring() throws RemoteException{
				// Called by the agent when he stops running
				// do whatever you want here
			}
		};
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

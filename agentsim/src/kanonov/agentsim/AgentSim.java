package kanonov.agentsim;

import dt.processor.Processor;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.CompoundButton;
import android.widget.*;

public class AgentSim extends Activity implements ServiceConnection{
	private static final String PROCESSOR_INTENT_ACTION = "dt.processor.kbta.action.PROCESSOR_SERVICE";
	
	private ToggleButton _controlButton;

	private Processor _processor;
	
	private Thread _thread;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		_controlButton = (ToggleButton)findViewById(R.id.control_button);
		_controlButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton button, boolean checked){
						if (checked){
							if (!bindService(new Intent(
									PROCESSOR_INTENT_ACTION)
									.addCategory("dt.agent.category.PROCESSOR"),
								AgentSim.this, BIND_AUTO_CREATE)){
								Toast.makeText(AgentSim.this, "Unable to connect to processor", Toast.LENGTH_SHORT);
								_controlButton.setChecked(true);
							}
						}else{
							unbindService(AgentSim.this);
						}
					}
				});

	}

	@Override
	public void onServiceConnected(ComponentName component, IBinder service){
		if (service == null){
			Toast.makeText(AgentSim.this, "Received null binder from processor", Toast.LENGTH_SHORT);
		}else{
			Toast.makeText(AgentSim.this, "Connected to processor", Toast.LENGTH_SHORT);
			_processor = Processor.Stub.asInterface(service);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName component){
		Toast.makeText(AgentSim.this, "Disconnected from processor", Toast.LENGTH_SHORT);
	}
}
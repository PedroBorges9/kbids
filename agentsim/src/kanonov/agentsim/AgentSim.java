package kanonov.agentsim;

import java.util.*;

import dt.fe.*;
import dt.processor.Processor;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;


public class AgentSim extends Activity implements ServiceConnection{
	private static final String PROCESSOR_INTENT_ACTION = "dt.processor.kbta.action.PROCESSOR_SERVICE";
	
	private ToggleButton _controlButton;
	private Button _plus, _minus;
	private TextView _interval;
	private Processor _processor;
	
	private Thread _thread;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		_interval = (TextView)findViewById(R.id.interval);
		_interval.setText("2");
		

		_plus = (Button)findViewById(R.id.plus);
		_plus.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v){
				_interval.setText(String.valueOf(getInterval() + 1));
			}
			
		});
		
		_minus = (Button)findViewById(R.id.minus);
		_minus.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v){
				_interval.setText(String.valueOf(Math.max(1, getInterval() - 1)));
			}
			
		});
		
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
								Toast.makeText(AgentSim.this, "Unable to connect to processor", Toast.LENGTH_SHORT).show();
								_controlButton.setChecked(false);
							}
						}else{
							unbindService(AgentSim.this);
							if (_thread!=null) _thread.interrupt();
							_thread = null;
							Toast.makeText(AgentSim.this, "Disconnected from processor", Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	@Override
	public void onServiceConnected(ComponentName component, IBinder service){
		if (service == null){
			Toast.makeText(AgentSim.this, "Received null binder from processor", Toast.LENGTH_SHORT);
		}else{
			Toast.makeText(AgentSim.this, "Connected to processor", Toast.LENGTH_SHORT).show();
			_processor = Processor.Stub.asInterface(service);

			if (_thread!=null) _thread.interrupt();
			_thread = new Thread(){
				@Override
				public void run(){
					int loop = 0;
					while (!isInterrupted() && loop++ < 4){
						SystemClock.sleep(getInterval() * 1000);
						
						if (_processor != null){
							List<MonitoredData> list = new ArrayList<MonitoredData>();
							ParcelableDate d = new ParcelableDate();
							Random r=new Random();
							MonitoredData md;
							Bundle extras;
							
							list.add(md = new MonitoredData("CPU_Usage", r.nextInt(10)+81, d));
							extras = new Bundle();
							extras.putInt("CPU_Pid", 55 + loop);
							md.setExtras(extras);
							list.add(md = new MonitoredData("Garbage_Collections", r.nextInt(20)+101, d));
							extras = new Bundle();
							extras.putInt("GC_Pid", 99 + loop);
							md.setExtras(extras);
							list.add(md = new MonitoredData("Running_Processes", r.nextInt(20)+30, d));
							list.add(md = new MonitoredData("Minor_Page_Faults", r.nextInt(20)+301, d));
							list.add(md = new MonitoredData("Context_Switches", r.nextInt(20)+1501, d));
							extras = new Bundle();
							extras.putInt("CS_Pid", 19 + loop);
							md.setExtras(extras);

//							list.add(md = new MonitoredData("Keyboard_Opening", 1, d));
//							extras = new Bundle();
//							extras.putLongArray("Events", 
//									new long[]{d.getTime() - 5000, d.getTime() - 3000, d.getTime()});
//							md.setExtras(extras);
							try{
								_processor.receiveMonitoredData(list);
							}catch(RemoteException e){
								e.printStackTrace();
							}
						}
					}
				}
			};
			_thread.start();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName component){
		Toast.makeText(AgentSim.this, "Processor crashed", Toast.LENGTH_SHORT).show();
		_processor = null;
	}

	private int getInterval(){
		return Integer.parseInt(_interval.getText().toString());
	}
}
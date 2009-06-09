package dt.processor.kbta;

import static dt.processor.kbta.Env.TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kxmlrpc.XmlRpcClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import dt.processor.kbta.ontology.instances.Element;

@SuppressWarnings("unchecked")
public class NetProtectConnection{

	private static final String AGENT_ID = "agent_id";

	private static final String SECRET = "secret";

	private static final String NETPROTECT_URL = "netprotect_url";

	private final Context _agentContext;

	private XmlRpcClient _server;

	private WifiManager _wm;

	public NetProtectConnection(Context context, String agentPackageName)
			throws NameNotFoundException{
		_agentContext = context.createPackageContext(agentPackageName, 0);

		_wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}

	public void sendRelatedElementsOf(Element e) throws Exception{
		SharedPreferences agentPrefs = PreferenceManager
				.getDefaultSharedPreferences(_agentContext);
		SharedPreferences kbtaPrefs = Env.getSharedPreferences();

		boolean connected = (_wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
				&& _wm.getConnectionInfo().getNetworkId() != -1;
		if (!connected){
			return;
		}

		if (kbtaPrefs.getBoolean(Env.SEND_ELEMENTS_TO_NETPROTECT, false)){
			if (_server == null){
				String serverURL = agentPrefs.getString(NETPROTECT_URL, null);
				if (serverURL == null){
					Log.w(TAG,
						"Can't send elements to NetProtect, the server URL is not set");
					return;
				}

				_server = new XmlRpcClient(serverURL);
			}

			String agentId = agentPrefs.getString(AGENT_ID, null);
			String secret = agentPrefs.getString(SECRET, null);
			if (agentId == null || secret == null){
				Log.w(TAG,
					"Can't send elements to NetProtect, the agent is not registered");
				return;
			}

			List<Map> elements = new ArrayList<Map>();
			e.toNetProtectElement(elements);
//			for (Map m : elements){
//				System.out.println(m);
//			}
			if (!elements.isEmpty()){
				_server
						.execute(
							"bgu.dt.netprotect.external.CalculatedDataGateway.sendCalculatedData",
							agentId, secret, elements);
			}
		}
	}

}

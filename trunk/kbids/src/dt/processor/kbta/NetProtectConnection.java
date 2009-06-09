package dt.processor.kbta;

import static dt.processor.kbta.Env.TAG;

import java.util.List;
import java.util.Map;

import org.kxmlrpc.XmlRpcClient;

import dt.processor.kbta.container.AllInstanceContainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressWarnings("unchecked")
public class NetProtectConnection{
	
	private static final String AGENT_ID = "agent_id";

	private static final String SECRET = "secret";

	private static final String NETPROTECT_URL = "netprotect_url";

	private final Context _agentContext;

	private XmlRpcClient _server;

	public NetProtectConnection(Context context) throws Exception{
		_agentContext = context.createPackageContext("dt.agent", 0);
	}

	public void sendElements(AllInstanceContainer aic) throws Exception{
		SharedPreferences agentPrefs = PreferenceManager
				.getDefaultSharedPreferences(_agentContext);
		SharedPreferences kbtaPrefs = Env.getSharedPreferences();

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

			List<? extends Map> elements = constructElementList(aic);
			if (elements != null && !elements.isEmpty()){
				_server
						.execute(
							"bgu.dt.netprotect.external.CalculatedDataGateway.sendCalculatedData",
							agentId, secret, elements);
			}
		}
	}

	private List<? extends Map> constructElementList(AllInstanceContainer aic){
		return null;
	}

}

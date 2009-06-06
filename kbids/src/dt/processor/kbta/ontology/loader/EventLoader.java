package dt.processor.kbta.ontology.loader;

import static dt.processor.kbta.ontology.loader.OntologyLoader.TAG;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.ontology.defs.EventDef;

public class EventLoader{
	private final HashMap<String, EventDef> _events;

	public EventLoader(HashMap<String, EventDef> events){
		_events = events;
	}

	public void parseEvents(XmlPullParser xpp) throws XmlPullParserException, IOException{
		for (int eventType = xpp.next(); eventType != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Events"); eventType = xpp.next()){

			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Event")){
				String name = xpp.getAttributeValue(null, "name");
				if (TextUtils.isEmpty(name)){
					Log.e(TAG, "Missing name for an event");
					continue;
				}
				EventDef ed = new EventDef(name);
				_events.put(ed.getName(), ed);
			}
		}
	}
}

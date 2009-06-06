package dt.processor.kbta.ontology.loader;

import static android.text.TextUtils.isEmpty;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.defs.patterns.LinearPatternDef;
import dt.processor.kbta.settings.Model;
import dt.processor.kbta.util.FileChangeTracker;
import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.FileChangeTracker.ChangeInfo;

public class OntologyLoader{
	public static final String TAG = "OntologyLoader";

	private static final String DEFAULT_NAME = "Android";

	private static final String DEFAULT_VERSION = "0";

	private static final long DEFAULT_ELEMENT_TIMEOUT = 10000;

	private final HashMap<String, PrimitiveDef> _primitives;

	private final HashMap<String, EventDef> _events;

	private final ArrayList<ContextDef> _contexts;

	private final ArrayList<StateDef> _states;

	private final ArrayList<TrendDef> _trends;

	private final ArrayList<LinearPatternDef> _patterns;

	private Long _elementTimeout;

	private String _ontologyName;

	private String _version;

	public OntologyLoader(){
		_primitives = new HashMap<String, PrimitiveDef>();
		_events = new HashMap<String, EventDef>();
		_contexts = new ArrayList<ContextDef>();
		_states = new ArrayList<StateDef>();
		_trends = new ArrayList<TrendDef>();
		_patterns = new ArrayList<LinearPatternDef>();
	}

	public Ontology loadOntology(Context context){
		try{
			// Checking whether the default ontology has changed
			FileChangeTracker fct = FileChangeTracker.getFileChangeTracker(context);
			ChangeInfo ci = fct.hasBeenModified(context, Model.ONTOLOGY, Model.ONTOLOGY);

			File ontologyFile = Model.getOntologyModelFile(context);
			// Overriding the model file with the default one
			// in case it hasn't been copied to the private storage yet
			// or the default model (inside the apk) has been modified and
			// it is not the first time the model is being loaded
			if (!ontologyFile.exists() || (!ci.firstTimeTracked && !ci.hasntBeenModified)){
				Log.i(TAG, "Loading default ontology model");
				if (!Model.copyDefaultModelFile(context, ontologyFile)){
					Log.e(TAG, "Unable to load the default Threats");
					return null;
				}
			}
			fct.updateFileStatus(ci);

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new FileReader(ontologyFile));

			for (int eventType = xpp.getEventType(); eventType != END_DOCUMENT; eventType = xpp
					.next()){
				String tag;
				if (eventType != START_TAG || isEmpty(tag = xpp.getName())){
					continue;
				}

				if (tag.equalsIgnoreCase("Ontology")){
					parseOntologyTag(xpp);
				}else if (tag.equalsIgnoreCase("Primitives")){
					new PrimitiveLoader(_primitives).parsePrimitives(xpp);
				}else if (tag.equalsIgnoreCase("Events")){
					new EventLoader(_events).parseEvents(xpp);
				}else if (tag.equalsIgnoreCase("Contexts")){
					new ContextLoader(_contexts).parseContexts(xpp);
				}else if (tag.equalsIgnoreCase("States")){
					new StateLoader(_states).parseStates(xpp);
				}else if (tag.equalsIgnoreCase("Trends")){
					new TrendLoader(_trends).parseTrends(xpp);
				}else if (tag.equalsIgnoreCase("Patterns")){
					new PatternLoader(_patterns).parsePatterns(xpp);
				}
			}

			if (_elementTimeout == null){
				Log.w(TAG,
					"Missing/corrupt \"elementTimeout\" attribute in Ontology element, using default: "
							+ DEFAULT_ELEMENT_TIMEOUT);
				_elementTimeout = DEFAULT_ELEMENT_TIMEOUT;
			}

			if (isEmpty(_ontologyName)){
				Log.w(TAG,
					"Missing/corrupt \"name\" attribute in Ontology element, using default: "
							+ DEFAULT_NAME);
				_ontologyName = DEFAULT_NAME;
			}

			if (isEmpty(_version)){
				Log.w(TAG,
					"Missing/corrupt \"version\" attribute in Ontology element, using default: "
							+ DEFAULT_VERSION);
				_version = DEFAULT_VERSION;
			}

			return new Ontology(_primitives, _events, _contexts, _states, _trends,
					_patterns, _elementTimeout, _ontologyName, _version);
		}catch(Exception e){
			Log.e(TAG, "Error while loading Ontology", e);
		}

		return null;
	}

	private void parseOntologyTag(XmlPullParser xpp){
		_ontologyName = xpp.getAttributeValue(null, "name");
		_version = xpp.getAttributeValue(null, "version");

		try{
			long elementTimeout = new ISODuration(xpp.getAttributeValue(null,
				"elementTimeout")).toMillis();
			if (elementTimeout > 1000){
				_elementTimeout = elementTimeout;
			}else{
				Log.w(TAG, "Element timeout must be at least 1 second");
			}
		}catch(Exception e){
			Log.w(TAG,
				"Missing/corrupt \"elementTimeout\" attribute in Ontology element", e);
		}
	}
	
}

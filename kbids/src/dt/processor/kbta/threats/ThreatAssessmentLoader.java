package dt.processor.kbta.threats;

import static android.text.TextUtils.isEmpty;
import static dt.processor.kbta.util.XmlParser.parseDurationCondition;
import static dt.processor.kbta.util.XmlParser.parseSymbolicValueCondition;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.Env;
import dt.processor.kbta.settings.Model;
import dt.processor.kbta.util.FileChangeTracker;
import dt.processor.kbta.util.FileChangeTracker.ChangeInfo;

public class ThreatAssessmentLoader{
	private static final String TAG = "ThreatAssessmentLoader";

	private static final String DEFAULT_NAME = "Android";

	private static final String DEFAULT_VERSION = "0";

	private String _threatsName;
	
	private String _version;

	private final TreeMap<String, ThreatAssessment> _assessments;

	public ThreatAssessmentLoader(){
		_assessments = new TreeMap<String, ThreatAssessment>();
	}

	public ThreatAssessor loadThreatAssessments(Context context){
		try{
			// Checking whether the default threat assessment model has changed
			FileChangeTracker fct = FileChangeTracker.getFileChangeTracker(context);
			ChangeInfo ci = fct.hasBeenModified(context, Model.THREAT_ASSESSMENTS,
				Model.THREAT_ASSESSMENTS);

			File threatsFile = Model.getThreatsModelFile(context);
			// Overriding the model file with the default one
			// in case it hasn't been copied to the private storage yet
			// or the default model (inside the apk) has been modified and
			// it is not the first time the model is being loaded
			boolean changed = !ci.firstTimeTracked && !ci.hasntBeenModified;
			if (!threatsFile.exists() || (changed)){
				if (changed){
					Env.getSharedPreferences().edit().clear().commit();
				}
				Log.i(TAG, "Loading default threat assessment model");
				if (!Model.copyDefaultModelFile(context, threatsFile)){
					Log.e(TAG, "Unable to load the default Threats");
					return null;
				}
			}
			fct.updateFileStatus(ci);

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new FileReader(threatsFile));

			for (int eventType = xpp.getEventType(); eventType != END_DOCUMENT; eventType = xpp
					.next()){
				if (eventType == START_TAG){
					String name = xpp.getName();
					if (TextUtils.isEmpty(name))
						continue;
					else if (name.equalsIgnoreCase("Assessments")){
						_threatsName = xpp.getAttributeValue(null, "name");
						_version = xpp.getAttributeValue(null, "version");
						parseAssessments(xpp);
					}
				}
			}

			if (isEmpty(_threatsName)){
				Log.w(TAG,
					"Missing/corrupt \"name\" attribute in Assessments element, using default: "
							+ DEFAULT_NAME);
				_threatsName = DEFAULT_NAME;
			}
			
			if (isEmpty(_version)){
				Log.w(TAG,
					"Missing/corrupt \"version\" attribute in Ontology element, using default: "
							+ DEFAULT_VERSION);
				_version = DEFAULT_VERSION;
			}

			return new ThreatAssessor(_assessments, _threatsName, _version);
		}catch(Exception e){
			Log.e(TAG, "Error while loading Threat assessments", e);
			return null;
		}
	}

	private void parseAssessments(XmlPullParser xpp) throws Exception{
		int eventType;
		GeneratedFrom generatedFrom = null;

		while ((eventType = xpp.next()) != END_TAG
				|| !xpp.getName().equalsIgnoreCase("Assessments")){
			if (eventType == START_TAG && "Assessment".equalsIgnoreCase(xpp.getName())){
				String title = xpp.getAttributeValue(null, "title");
				String description = xpp.getAttributeValue(null, "description");
				String baseCertaintyStr = xpp.getAttributeValue(null, "baseCertainty");

				if (isEmpty(title)){
					Log.e(TAG, "Faulty threat assessment without title");
					continue;
				}

				int baseCertainty;
				if (!(TextUtils.isDigitsOnly(baseCertaintyStr)
						&& (baseCertainty = Integer.parseInt(baseCertaintyStr)) > 0 && baseCertainty <= 100)){
					Log.e(TAG, "Faulty threat asssessment base certainty");
					continue;
				}

				String sMonitored = xpp.getAttributeValue(null, "monitored");
				boolean monitored = false;
				if (sMonitored == null || Boolean.parseBoolean(sMonitored)){
					monitored = true;
				}

				while ((eventType = xpp.next()) != END_TAG
						|| !xpp.getName().equalsIgnoreCase("Assessment")){
					if (eventType != START_TAG){
						continue;
					}
					generatedFrom = parseGeneratedFrom(xpp);
				}

				if (generatedFrom != null){
					ThreatAssessment threatAssessment = new ThreatAssessment(title,
							description, baseCertainty, monitored, generatedFrom);
					_assessments.put(title, threatAssessment);
				}
			}
		}
	}

	private GeneratedFrom parseGeneratedFrom(XmlPullParser xpp) throws Exception{
		SymbolicValueCondition symbolicValueCondition = null;
		DurationCondition durationCondition = null;
		int eventType;

		String type = xpp.getName();
		String elementName = xpp.getAttributeValue(null, "name");
		if (isEmpty(type) || isEmpty(elementName)){
			Log.e(TAG, "Missing threat assessment element type (" + type + ")/name("
					+ elementName + ")");
			return null;
		}

		while ((eventType = xpp.next()) != END_TAG
				|| !xpp.getName().equalsIgnoreCase(type)){
			if (eventType != START_TAG){
				continue;
			}

			if ("SymbolicValueCondition".equalsIgnoreCase(xpp.getName())){
				symbolicValueCondition = parseSymbolicValueCondition(xpp);
				eventType = xpp.getEventType();
			}

			if ("DurationCondition".equalsIgnoreCase(xpp.getName())){
				durationCondition = parseDurationCondition(xpp);
			}
		}

		if (durationCondition == null){
			Log.e(TAG, "Missing threat assessment duration condition");
			return null;
		}
		if (type.equalsIgnoreCase("State")){
			return new GeneratedFromState(elementName, symbolicValueCondition,
					durationCondition);
		}else if (type.equalsIgnoreCase("Trend")){
			return new GeneratedFromTrend(elementName, symbolicValueCondition,
					durationCondition);
		}else if (type.equalsIgnoreCase("LinearPattern")){
			return new GeneratedFromLinearPattern(elementName, symbolicValueCondition,
					durationCondition);
		}
		return null;
	}
}

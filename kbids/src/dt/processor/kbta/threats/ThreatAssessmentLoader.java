package dt.processor.kbta.threats;

import static android.text.TextUtils.isEmpty;
import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static dt.processor.kbta.util.XmlParser.*;
import java.io.IOException;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.R;

public class ThreatAssessmentLoader{
	private static final String TAG = "ThreatAssessmentLoader";

	private ThreatAssessor _ta;

	public ThreatAssessor loadThreatAssessments(Context context){
		_ta = new ThreatAssessor();

		int eventType;
		try{
			XmlPullParser xpp = context.getResources().getXml(R.xml.threat_asssessments);

			eventType = xpp.getEventType();

			while (eventType != END_DOCUMENT){

				if (eventType == START_TAG){
					String name = xpp.getName();
					if (TextUtils.isEmpty(name))
						continue;
					else if (name.equalsIgnoreCase("Assessments")){
						parseAssessments(xpp);
					}
				}

				eventType = xpp.next();
			}

			return _ta;
		}catch(Exception e){
			Log.e(TAG, "Error while loading Threat assessments", e);
		}

		return null;
	}

	private void parseAssessments(XmlPullParser xpp) throws XmlPullParserException,
			IOException{

		// System.out.println("******parseAssessments******");
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
				
				String sMonitored= xpp.getAttributeValue(null, "monitored");
				boolean monitored=false;
				if(sMonitored==null || Boolean.parseBoolean(sMonitored)){
					monitored=true;
				}

				while ((eventType = xpp.next()) != END_TAG
						|| !xpp.getName().equalsIgnoreCase("Assessment")){
					generatedFrom = parseGeneratedFrom(xpp);
				}

				if (generatedFrom != null){
					ThreatAssessment threatAssessment = new ThreatAssessment(title,
							description, baseCertainty,monitored, generatedFrom);
					_ta.addThreatAssessment(threatAssessment);
				}
			}
		}
		// System.out.println(_ta);
	}

	private GeneratedFrom parseGeneratedFrom(XmlPullParser xpp)
			throws XmlPullParserException, IOException{
		SymbolicValueCondition symbolicValueCondition = null;
		DurationCondition durationCondition = null;
		int eventType;

		for (eventType = xpp.getEventType(); eventType != START_TAG; eventType = xpp
				.next());

		String type = xpp.getName();
		String elementName = xpp.getAttributeValue(null, "name");
		if (TextUtils.isEmpty(type) || TextUtils.isEmpty(elementName)){
			Log.e(TAG, "Missing threat assessment element type/name");
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
		}else if (type.equalsIgnoreCase("Pattern")){
			return new GeneratedFromPattern(elementName, symbolicValueCondition,
					durationCondition);
		}
		return null;

	}

	
}

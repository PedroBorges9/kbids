package dt.processor.kbta.threats;

import static org.xmlpull.v1.XmlPullParser.*;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import dt.processor.kbta.R;
import dt.processor.kbta.ontology.Ontology;
import dt.processor.kbta.util.InvalidDateException;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class ThreatAssessmentLoader {
	private static final String TAG = "ThreatAssessmentLoader";
	private ThreatAssessor _ta;

	public ThreatAssessor loadThreatAssessments(Context context) {
		_ta = new ThreatAssessor();

		System.out.println("loadThreatAssessments");

		int eventType;
		try {
			XmlPullParser xpp = context.getResources().getXml(
					R.xml.threat_asssessments);

			eventType = xpp.getEventType();

			while (eventType != END_DOCUMENT) {

				if (eventType == START_TAG) {
					String name = xpp.getName();
					if (TextUtils.isEmpty(name))
						continue;
					else if (name.equalsIgnoreCase("Assessments")) {
						parseAssessments(xpp);
					}
				}

				eventType = xpp.next();
			}

			return _ta;
		} catch (Exception e) {
			Log.e(TAG, "Error while loading Threat assessments", e);
		}

		return null;
	}

	private void parseAssessments(XmlPullParser xpp)
			throws XmlPullParserException, IOException {

		System.out.println("******parseAssessments******");
		int eventType;
		ArrayList<String> symbolicValueCondition;
		ArrayList<Duration> durationCondition;
		ArrayList<GeneratedFrom> generatedFromCollection = null;

		while ((eventType = xpp.next()) != END_TAG
				|| !xpp.getName().equalsIgnoreCase("Assessments")) {
			if (eventType == START_TAG
					&& "Assessment".equalsIgnoreCase(xpp.getName())) {

				String title = xpp.getAttributeValue(null, "title");
				String description = xpp.getAttributeValue(null, "description");

				// System.out.println("title=" + title + " description="
				// +description);
				while ((eventType = xpp.next()) != END_TAG
						|| !xpp.getName().equalsIgnoreCase("Assessment")) {
					generatedFromCollection = parseGeneratedFrom(xpp, eventType);

				}
				ThreatAssessment threatAssessment = new ThreatAssessment(title,
						description, 100, generatedFromCollection);
				_ta.addThreatAssessment(threatAssessment);
			}
		}
		System.out.println(_ta);
	}

	private ArrayList<GeneratedFrom> parseGeneratedFrom(XmlPullParser xpp,
			int eventType) throws XmlPullParserException, IOException {
		String type;
		String nameOfType;
		SymbolicValueCondition symbolicValueCondition = null;
		DurationCondition durationCondition = null;
		GeneratedFrom gf = null;
		ArrayList<GeneratedFrom> generatedFromCollection = new ArrayList<GeneratedFrom>();

		if (eventType == START_TAG
				&& "GeneratedFrom".equalsIgnoreCase(xpp.getName())) {

			while ((eventType = xpp.next()) != END_TAG
					|| !xpp.getName().equalsIgnoreCase("GeneratedFrom")) {

				if (eventType == START_TAG) {
					type = xpp.getName();
					nameOfType = xpp.getAttributeValue(null, "name");

					// System.out.println("type="+type+" nameOfType="
					// +nameOfType);

					while ((eventType = xpp.next()) != END_TAG
							|| !xpp.getName().equalsIgnoreCase(type)) {

						if (eventType == START_TAG
								&& "SymbolicValueCondition"
										.equalsIgnoreCase(xpp.getName())) {
							symbolicValueCondition = parseSymbolicValueCondition(xpp);
							eventType = xpp.getEventType();
							// System.out.println("CH\n"+symbolicValueCondition+"ECH\n");//?
						}

						if (eventType == START_TAG
								&& "DurationCondition".equalsIgnoreCase(xpp
										.getName())) {
							durationCondition = parseDurationCondition(xpp);
						}

					}

					gf = new GeneratedFrom(type, nameOfType,
							symbolicValueCondition, durationCondition);

				}

				generatedFromCollection.add(gf);
			}

		}
		// System.out.println("CH\n"+generatedFromCollection+"ECH\n");//?
		return generatedFromCollection;
	}

	private DurationCondition parseDurationCondition(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		String tag;
		int eventType = xpp.getEventType();
		DurationCondition durationCondition = null;
		ArrayList<Duration> durationConditionList = new ArrayList<Duration>();

		while ((eventType = xpp.next()) != END_TAG
				|| !(tag = xpp.getName()).equalsIgnoreCase("DurationCondition")) {
			if (eventType == START_TAG
					&& "Duration".equalsIgnoreCase(xpp.getName())) {
				String min = null, max = null;
				try {
					Duration dur = new Duration(min = xpp.getAttributeValue(
							null, "min"), max = xpp.getAttributeValue(null,
							"max"));
					durationConditionList.add(dur);
				} catch (InvalidDateException e) {
					Log.e(TAG,
							"Improper duration in duration condition, min = "
									+ min + ", max = " + max);
				}
				// System.out.println("min= "+xpp.getAttributeValue(null,
				// "min")+"max= "+xpp.getAttributeValue(null, "max"));
			}
		}
		if (!durationConditionList.isEmpty()) {
			durationCondition = new DurationCondition(durationConditionList);
		}

		return durationCondition;

	}

	private SymbolicValueCondition parseSymbolicValueCondition(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		SymbolicValueCondition symbolicValueCondition = null;
		ArrayList<String> symbolicValueConditionList = new ArrayList<String>();

		while ((eventType = xpp.next()) != END_TAG
				|| !xpp.getName().equalsIgnoreCase("SymbolicValueCondition")) {
			if (eventType == START_TAG
					&& "Value".equalsIgnoreCase(xpp.getName())) {

				symbolicValueConditionList.add(xpp.getAttributeValue(null,
						"name"));
				// System.out.println("value
				// name="+xpp.getAttributeValue(null, "name"));

			}
		}
		if (!symbolicValueConditionList.isEmpty()) {
			symbolicValueCondition = new SymbolicValueCondition(
					symbolicValueConditionList);
		}
		// ?? System.out.println("CHECK\n"+symbolicValueCondition);

		return symbolicValueCondition;
	}
}

package dt.processor.kbta.util;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;

public class XmlParser{
	private static final String TAG = "ThreatAssessmentLoader/patterns";
	
	public static DurationCondition parseDurationCondition(XmlPullParser xpp){
		DurationCondition durationCondition;
		String min = null;
		String max = null;
		try{
			durationCondition = new DurationCondition(min = xpp
					.getAttributeValue(null, "min"), max = xpp.getAttributeValue(
				null, "max"));
		}catch(Exception e){
			Log.e(TAG, "Improper duration in duration condition, min = " + min
					+ ", max = " + max, e);
			return null;
		}
		return durationCondition;
	}

	public static SymbolicValueCondition parseSymbolicValueCondition(XmlPullParser xpp)
			throws XmlPullParserException, IOException{
		int eventType;
		SymbolicValueCondition symbolicValueCondition = null;
		HashSet<String> symbolicValueConditions = new HashSet<String>();

		while ((eventType = xpp.next()) != END_TAG
				|| !xpp.getName().equalsIgnoreCase("SymbolicValueCondition")){
			if (eventType == START_TAG && "Value".equalsIgnoreCase(xpp.getName())){
				String value = xpp.getAttributeValue(null, "name");
				if (value != null){
					symbolicValueConditions.add(value);
				}
			}
		}
		if (!symbolicValueConditions.isEmpty()){
			symbolicValueCondition = new SymbolicValueCondition(symbolicValueConditions);
		}

		return symbolicValueCondition;
	}

}

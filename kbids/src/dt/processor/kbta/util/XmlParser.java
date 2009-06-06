package dt.processor.kbta.util;

import static android.text.TextUtils.isEmpty;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;

public class XmlParser{

	public static ArrayList<String> parseNecessaryContexts(XmlPullParser xpp)
			throws XmlPullParserException, IOException{
		int eventType;
		ArrayList<String> necessaryContexts = new ArrayList<String>();

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("NecessaryContexts")){
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG && tag.equalsIgnoreCase("Context")){

				String name = xpp.getAttributeValue(null, "name");
				if (!isEmpty(name)){
					necessaryContexts.add(name);
				}
			}
		}
		// We will allow no contexts for convenience purposes
		// if (necessaryContexts.isEmpty()){
		// return null;
		// }
		return necessaryContexts;
	}

	public static NumericRange parseNumericRange(XmlPullParser xpp, String TAG){
		String minS;
		String maxS;
		boolean minE = true;
		boolean maxE = true;
		double min;
		double max;
		// Parsing the minimum value
		minS = xpp.getAttributeValue(null, "minE");
		if (minS == null){
			minS = xpp.getAttributeValue(null, "min");
			minE = false;
		}
		if ("*".equalsIgnoreCase(minS)){
			min = Double.NEGATIVE_INFINITY;
		}else{
			try{
				min = Double.parseDouble(minS);
			}catch(NumberFormatException e){
				Log.e(TAG, "Erroneous minimum " + "value for numeric range: ", e);
				return null;
			}
		}

		// Parsing the maximum value
		maxS = xpp.getAttributeValue(null, "maxE");
		if (maxS == null){
			maxS = xpp.getAttributeValue(null, "max");
			maxE = false;
		}
		if ("*".equalsIgnoreCase(maxS)){
			max = Double.POSITIVE_INFINITY;
		}else{
			try{
				max = Double.parseDouble(maxS);
			}catch(NumberFormatException e){
				Log.e(TAG, "Erroneous maximum " + "value for numeric range: ", e);
				return null;
			}
		}
		return new NumericRange(min, max, minE, maxE);
	}

	public static DurationCondition parseDurationCondition(XmlPullParser xpp, String TAG){
		DurationCondition durationCondition;
		String min = null;
		String max = null;
		try{
			durationCondition = new DurationCondition(min = xpp.getAttributeValue(null,
				"min"), max = xpp.getAttributeValue(null, "max"));
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

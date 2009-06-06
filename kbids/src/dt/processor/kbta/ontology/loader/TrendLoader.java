package dt.processor.kbta.ontology.loader;

import static dt.processor.kbta.ontology.loader.OntologyLoader.TAG;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendMappingFunction;
import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.XmlParser;

public class TrendLoader{
	private final ArrayList<TrendDef> _trends;

	public TrendLoader(ArrayList<TrendDef> trends){
		_trends = trends;
	}

	public void parseTrends(XmlPullParser xpp) throws XmlPullParserException, IOException{
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Trends")){
			if (eventType == XmlPullParser.START_TAG
					&& "Trend".equalsIgnoreCase(xpp.getName())){

				TrendDef trendDef = parseTrend(xpp);
				if (trendDef != null){
					_trends.add(trendDef);
				}
			}
		}
	}

	private TrendDef parseTrend(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;
		String abstractedFrom = null;
		ArrayList<String> necessaryContexts = null;
		TrendMappingFunction mappingFunction = null;

		String name = xpp.getAttributeValue(null, "name");
		if (TextUtils.isEmpty(name)){
			Log.e(TAG, "Missing name for trend");
			return null;
		}

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Trend")){
			if (eventType == XmlPullParser.START_TAG){
				if ("AbstractedFrom".equalsIgnoreCase(xpp.getName())){
					abstractedFrom = xpp.getAttributeValue(null, "name");
					if (TextUtils.isEmpty(abstractedFrom)){
						Log.e(TAG, "Missing name for an abstracted-from in the trend: "
								+ name);
						return null;
					}
				}else if ("NecessaryContexts".equalsIgnoreCase(xpp.getName())){
					necessaryContexts = XmlParser.parseNecessaryContexts(xpp);
				}else if ("MappingFunction".equalsIgnoreCase(xpp.getName())){
					try{
						double threshold = Double.parseDouble(xpp.getAttributeValue(null,
							"threshold"));
						double angle = Double.parseDouble(xpp.getAttributeValue(null,
							"angle"));
						long maxGap = new ISODuration(xpp.getAttributeValue(null,
							"maxGap")).toMillis();
						mappingFunction = new TrendMappingFunction(threshold, angle,
								maxGap);
					}catch(NumberFormatException e){
						Log.e(TAG,
							"Corrupt treshold or angle in the mapping function for the trend: "
									+ name, e);
						return null;
					}catch(Exception e){
						Log.e(TAG,
							"Corrupt maxgap value in the mapping function for the trend: "
									+ name, e);
						return null;
					}
				}
			}
		}

		if (abstractedFrom == null || necessaryContexts == null
				|| mappingFunction == null){
			return null;
		}
		return new TrendDef(name, abstractedFrom, necessaryContexts, mappingFunction);
	}
}

package dt.processor.kbta.threats;

import static dt.processor.kbta.KBTAProcessorService.TAG;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import dt.processor.kbta.R;
import dt.processor.kbta.ontology.Ontology;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class ThreatAssessmentLoader {
	private ThreatAssessor _ta;

	public ThreatAssessor loadThreatAssessments(Context context) {
		_ta = new ThreatAssessor();
		
		System.out.println("loadThreatAssessments");

		int eventType;
		try {
			XmlPullParser xpp = context.getResources().getXml(
					R.xml.threat_asssessments);

			eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {
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
			Log.e(TAG, "Error while loading Ontology", e);
		}

		return null;
	}

	private void parseAssessments(XmlPullParser xpp) throws XmlPullParserException,
	IOException{
		
		System.out.println("******parseAssessments******");
		int eventType;
		String title;
		String type=null;
		String nameOfType=null;
		String description=null;
		GeneratedFrom gf=null;
		ArrayList<String> symbolicValueCondition;
		ArrayList<Duration> durationCondition;
		String name;
		String tag;
		ThreatAssessment threatAssessment;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !(tag = xpp.getName()).equalsIgnoreCase("Assessments")) {
			if (eventType == XmlPullParser.START_TAG
					&& "Assessment".equalsIgnoreCase(xpp.getName())) {
				
				title =  xpp.getAttributeValue(null, "title");
				description =  xpp.getAttributeValue(null, "description");
				
				
			//	System.out.println("title=" + title + " description=" +description);

				while ((eventType = xpp.next()) != XmlPullParser.END_TAG
						|| !(tag = xpp.getName()).equalsIgnoreCase("Assessment")) {
					if (eventType == XmlPullParser.START_TAG
							&& "GeneratedFrom".equalsIgnoreCase(xpp.getName())) {
						
						while ((eventType = xpp.next()) != XmlPullParser.END_TAG
								|| !(tag = xpp.getName()).equalsIgnoreCase("GeneratedFrom")) {
							symbolicValueCondition = new ArrayList<String>();
							durationCondition = new ArrayList<Duration>();
				
							if (eventType == XmlPullParser.START_TAG){
								type = xpp.getName();
								nameOfType = xpp.getAttributeValue(null, "name");
								
							//	System.out.println("type="+type+" nameOfType=" +nameOfType);
								

								while ((eventType = xpp.next()) != XmlPullParser.END_TAG
										|| !(tag = xpp.getName()).equalsIgnoreCase(type)) {
									if (eventType == XmlPullParser.START_TAG
											&& "SymbolicValueCondition".equalsIgnoreCase(xpp.getName())) {
										while ((eventType = xpp.next()) != XmlPullParser.END_TAG
												|| !(tag = xpp.getName()).equalsIgnoreCase("SymbolicValueCondition")) {
											if (eventType == XmlPullParser.START_TAG
													&& "Value".equalsIgnoreCase(xpp.getName())) {
		
												symbolicValueCondition.add(xpp.getAttributeValue(null, "name"));
											//	System.out.println("value name="+xpp.getAttributeValue(null, "name"));
								
											}
										}
						
									}
									
									if (eventType == XmlPullParser.START_TAG
											&& "DurationCondition".equalsIgnoreCase(xpp.getName())) {
										while ((eventType = xpp.next()) != XmlPullParser.END_TAG
												|| !(tag = xpp.getName()).equalsIgnoreCase("DurationCondition")) {
											if (eventType == XmlPullParser.START_TAG
													&& "Duration".equalsIgnoreCase(xpp.getName())) {
												Duration dur = new Duration(xpp.getAttributeValue(null, "min"),
														xpp.getAttributeValue(null, "max"));
												durationCondition.add(dur);
										//		System.out.println("min= "+xpp.getAttributeValue(null, "min")+"max= "+xpp.getAttributeValue(null, "max"));	
											}
										}
						
									}
						
								}
							}
							gf=new GeneratedFrom(type,nameOfType,symbolicValueCondition,durationCondition);
							
						}
						
					}
				}
				threatAssessment = new ThreatAssessment(title,description,100,gf);
				_ta.addThreatAssessment(threatAssessment);
			}
		}
		System.out.println(_ta);

		
	}
}

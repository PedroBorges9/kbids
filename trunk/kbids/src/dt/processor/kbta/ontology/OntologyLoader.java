
package dt.processor.kbta.ontology;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.string;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.R;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.Induction;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.threats.Duration;
import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.InvalidDateException;


/**
 * @author
 * 
 */
public class OntologyLoader {
	public static final String TAG = "OntologyLoader";
	private Ontology _ontology;

	public Ontology loadOntology(Context context) {
		_ontology = new Ontology();

		int eventType;
		try {
			XmlPullParser xpp = context.getResources().getXml(R.xml.ontology);

			eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
				} else if (eventType == XmlPullParser.START_TAG) {
					String name = xpp.getName();
					if (TextUtils.isEmpty(name))
						continue;
					else if (name.equalsIgnoreCase("Primitives")) {
						parsePrimitives(xpp);
					} else if (name.equalsIgnoreCase("Events")) {
						parseEvents(xpp);

					} else if (name.equalsIgnoreCase("Contexts")) {
						parseContexts(xpp);

					} else if (name.equalsIgnoreCase("States")) {

					} else if (name.equalsIgnoreCase("Trends")) {

					}

					else if (name.equalsIgnoreCase("Patterns")) {

					}
				} else if (eventType == XmlPullParser.END_TAG) {
				} else if (eventType == XmlPullParser.TEXT) {
				}
				eventType = xpp.next();
			}

			return _ontology;
		} catch (Exception e) {
			Log.e(TAG, "Error while loading Ontology", e);
		}

		return null;
	}

	private void parsePrimitives(XmlPullParser xmlOntology)
	throws XmlPullParserException, IOException {
		System.out.println("parsing primitives");
		String name;
		String minS;
		String maxS;
		boolean minE = true;
		boolean maxE = true;
		double min;
		double max;
		int eventType;
		String tag;
		PrimitiveDef p = null;
		eventType = xmlOntology.next();

		while (eventType != XmlPullParser.END_TAG
				|| !(tag = xmlOntology.getName())
				.equalsIgnoreCase("Primitives")) {
			tag = xmlOntology.getName();
			if (eventType == XmlPullParser.START_TAG
					&& tag.equalsIgnoreCase("Primitive")) {
				name = xmlOntology.getAttributeValue(null, "name");
				if (TextUtils.isEmpty(name)) {
					Log.e(TAG, "Missing name for a primitive");
					continue;
				}
				NumericRange range=NumericRange.ParseRange(xmlOntology);
				if (range!=null){
					p = new PrimitiveDef(name, range);
					_ontology.addPrimitiveDef(p);
				}
				else{
					Log.e(TAG, "Invalid numeric range for the primitive "+name);
				}
			}
			eventType = xmlOntology.next();
		}
	}

	private void parseEvents(XmlPullParser xmlOntology)
	throws XmlPullParserException, IOException {
		int eventType;
		String name;
		String tag;

		eventType = xmlOntology.next();

		while (eventType != XmlPullParser.END_TAG
				|| !(tag = xmlOntology.getName()).equalsIgnoreCase("Events")) {
			tag = xmlOntology.getName();
			if (eventType == XmlPullParser.START_TAG
					&& tag.equalsIgnoreCase("Event")) {
				name = xmlOntology.getAttributeValue(null, "name");
				if (TextUtils.isEmpty(name)) {
					Log.e(TAG, "Missing name for an event");
					continue;
				}

				EventDef ed = new EventDef(name);
				_ontology.addEventDef(ed);
			}
			eventType = xmlOntology.next();
		}
	}

	private void parseContexts(XmlPullParser xpp) throws XmlPullParserException,
	IOException {
		int eventType;


		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Contexts")) {
			if (eventType == XmlPullParser.START_TAG
					&& "Context".equals(xpp.getName())) {
				loadContext(xpp);
			}
		}
	}

	private void loadContext(XmlPullParser xpp) throws XmlPullParserException,
	IOException{
		int eventType;
		String tag;
		System.out.println("<Context name="
			+ xpp.getAttributeValue(null, "name") + ">");

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !(tag = xpp.getName()).equalsIgnoreCase("Context")) {
			if (eventType == XmlPullParser.START_TAG
					&& "Inductions".equals(xpp.getName())) {



				ArrayList<Induction> inductions= parseInductions(xpp);

			} else if ("Destructions".equals(xpp.getName())) {

			}
		}
	}

	private ArrayList<Induction> parseInductions(XmlPullParser xpp) throws XmlPullParserException,
	IOException{
		int eventType;
		String tag;
		System.out.println("Inductions");

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Inductions")) {

			if (eventType == XmlPullParser.START_TAG
					&& "Induction".equals(xpp.getName())) {


				loadInduction(xpp);
			}
			System.out.println("Induction");

			xpp.next();
			System.out.println(xpp.getName() + " name="
				+ xpp.getAttributeValue(null, "name"));
			xpp.next();
			xpp.next();
			System.out.println(xpp.getName() + " gap="
				+ xpp.getAttributeValue(null, "gap"));
			xpp.next();
			xpp.next();

			;

		}
		return null;
	}

	private Induction loadInduction(XmlPullParser xpp) throws XmlPullParserException,
	IOException{
		int eventType;
		String tag;
		String name=null;
		String type=null;
		String value=null;
		NumericRange range=null;
		String Relative=null;
		Long gap=null;
		System.out.println("Induction");

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Induction")){
			if (eventType == XmlPullParser.START_TAG){
				tag = xpp.getName();
				if ("Primitive".equalsIgnoreCase(tag)){
					type="Primitive";
					name=xpp.getAttributeValue(null, "name");
					if (name!=null){
						Log.e(TAG, "missing name for primitive based induction");
						return null;
					}
					range=NumericRange.ParseRange(xpp);
					if (range==null){
						Log.e(TAG, "invalid numeric range for " + name +" based induction");
						return null;
					}

				}else if ("State".equalsIgnoreCase(tag)){
					name=xpp.getAttributeValue(null, "name");
					if (name!=null){
						Log.e(TAG, "missing name for state based induction");
						return null;
					}
					value=xpp.getAttributeValue(null, "value");
					if (value==null){
						Log.e(TAG, "invalid symbolic value for " + name +" based induction");
						return null;
					}

				}
				else if ("Trend".equalsIgnoreCase(tag)){

					return null;
				}
				else if("Ends".equalsIgnoreCase(tag)){
					Relative=xpp.getAttributeValue(null, "relativeTo");
					if (!"End".equalsIgnoreCase(Relative) 
							|| !"Start".equalsIgnoreCase(Relative)){
						Relative=null;
					}
					try{
						gap=(new ISODuration(xpp.getAttributeValue(null, "gap"))).toMillis();
					}catch(InvalidDateException e){
						Log.e(TAG,"invalid ISO Duration in induction",e);
						return null;
					}

				}
			}
		}
		return null;
			
	}
}

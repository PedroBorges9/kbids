/**
 * 
 */
package dt.processor.kbta.ontology;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.R;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.PrimitiveDef;

import static dt.processor.kbta.KBTAProcessorService.TAG;

/**
 * @author
 * 
 */
public class OntologyLoader {
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
						// parseContext(xpp);

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

				// Parsing the minimum value
				minS = xmlOntology.getAttributeValue(null, "minE");
				if (minS == null) {
					minS = xmlOntology.getAttributeValue(null, "min");
					minE = false;
				}
				if ("*".equals(minS)) {
					min = Double.NEGATIVE_INFINITY;
				} else {
					try {
						min = Double.parseDouble(minS);
					} catch (NumberFormatException e) {
						Log.e(TAG, "Erroneous minimum "
								+ "value for primitive: " + name, e);
						continue;
					}
				}

				// Parsing the maximum value
				maxS = xmlOntology.getAttributeValue(null, "maxE");
				if (maxS == null) {
					maxS = xmlOntology.getAttributeValue(null, "max");
					maxE = false;
				}
				if ("*".equals(maxS)) {
					max = Double.POSITIVE_INFINITY;
				} else {
					try {
						max = Double.parseDouble(maxS);
					} catch (NumberFormatException e) {
						Log.e(TAG, "Erroneous maximum "
								+ "value for primitive: " + name, e);
						continue;
					}
				}

				p = new PrimitiveDef(name, min, minE, max, maxE);
				_ontology.addPrimitiveDef(p);
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

	private void parseContext(XmlPullParser xpp) throws XmlPullParserException,
			IOException {
		int eventType;
		String name;
		String tag;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !(tag = xpp.getName()).equalsIgnoreCase("Contexts")) {
			if (eventType == XmlPullParser.START_TAG
					&& "Context".equals(xpp.getName())) {
				System.out.println("<Context name="
						+ xpp.getAttributeValue(null, "name") + ">");

				while ((eventType = xpp.next()) != XmlPullParser.END_TAG
						|| !(tag = xpp.getName()).equalsIgnoreCase("Context")) {
					if (eventType == XmlPullParser.START_TAG
							&& "Inductions".equals(xpp.getName())) {
						System.out.println("Inductions");

						while ((eventType = xpp.next()) != XmlPullParser.END_TAG
								|| !(tag = xpp.getName())
										.equalsIgnoreCase("Inductions")) {

							if (eventType == XmlPullParser.START_TAG
									&& "Induction".equals(xpp.getName())) {
								System.out.println("Induction");

								while ((eventType = xpp.next()) != XmlPullParser.END_TAG
										|| !(tag = xpp.getName())
												.equalsIgnoreCase("Induction")) {
									if (eventType == XmlPullParser.START_TAG){
										tag = xpp.getName();
									}else{
										continue;
									}
									if ("Primitive".equalsIgnoreCase(tag)){
										
									}else{
										
									}
								}
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

					} else if ("Destructions".equals(xpp.getName())) {

					}
				}
			}
		}
	}

}

package dt.processor.kbta.ontology;

import static android.text.TextUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.R.string;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.R;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.abstractions.AbstractedFrom;
import dt.processor.kbta.ontology.defs.abstractions.ElementCondition;
import dt.processor.kbta.ontology.defs.abstractions.InterpolationFunction;
import dt.processor.kbta.ontology.defs.abstractions.MappingFunction;
import dt.processor.kbta.ontology.defs.abstractions.MappingFunctionEntry;
import dt.processor.kbta.ontology.defs.abstractions.PrimitiveCondition;
import dt.processor.kbta.ontology.defs.abstractions.StateCondition;
import dt.processor.kbta.ontology.defs.abstractions.StateDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.defs.context.Induction;
import dt.processor.kbta.ontology.instances.Element;
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
					//	 System.out.println("Before parseStates");
						 parseStates(xpp);
//						 System.out.println("Finish parse states");

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
		// System.out.println("parsing primitives");
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
				NumericRange range = NumericRange.parseRange(xmlOntology);
				if (range != null) {
					p = new PrimitiveDef(name, range);
					_ontology.addPrimitiveDef(p);
				} else {
					Log.e(TAG, "Invalid numeric range for the primitive "
							+ name);
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

	private void parseContexts(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
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
			IOException {
		int eventType;
		String tag;
		String contextName = null;
		ArrayList<Induction> inductions = null;

		contextName = xpp.getAttributeValue(null, "name");
		if (contextName == null) {
			Log.e(TAG, "No name for context");
			return;
		}

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !(tag = xpp.getName()).equalsIgnoreCase("Context")) {

			if (eventType == XmlPullParser.START_TAG
					&& "Inductions".equals(xpp.getName())) {

				inductions = parseInductions(xpp, contextName);

			} else if ("Destructions".equals(xpp.getName())) {

			}
		}
		if (contextName != null) {
			ContextDef cd = new ContextDef(contextName, inductions, null);
			_ontology.AddContextDefiners(cd);
		}
	}

	private ArrayList<Induction> parseInductions(XmlPullParser xpp,
			String context) throws XmlPullParserException, IOException {
		int eventType;
		String tag;
		ArrayList<Induction> inductions = new ArrayList<Induction>();
		Induction i = null;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Inductions")) {

			if (eventType == XmlPullParser.START_TAG
					&& "Induction".equals(xpp.getName())) {

				i = loadInduction(xpp, context);
				if (i != null)
					inductions.add(i);
			}
		}
		if (inductions.isEmpty())
			return null;
		return inductions;
	}

	private Induction loadInduction(XmlPullParser xpp, String context)
			throws XmlPullParserException, IOException {
		int eventType;
		String tag;
		String name = null;
		int type = -1;
		String value = null;
		NumericRange range = null;
		String relative = null;
		long gap = -1;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Induction")) {
			if (eventType == XmlPullParser.START_TAG) {
				tag = xpp.getName();
				if ("Primitive".equalsIgnoreCase(tag)) {
					type = Element.PRIMITIVE;
					name = xpp.getAttributeValue(null, "name");
					if (name == null) {
						Log
								.e(TAG,
										"missing name for primitive based induction");
						return null;
					}
					range = NumericRange.parseRange(xpp);
					if (range == null) {
						Log.e(TAG, "invalid numeric range for " + name
								+ " based induction");
						return null;
					}

				} else if ("State".equalsIgnoreCase(tag)) {
					type = Element.STATE;
					name = xpp.getAttributeValue(null, "name");
					if (name == null) {
						Log.e(TAG, "missing name for state based induction");
						return null;
					}
					value = xpp.getAttributeValue(null, "value");
					if (value == null) {
						Log.e(TAG, "invalid symbolic value for " + name
								+ " based induction");
						return null;
					}

				} else if ("Trend".equalsIgnoreCase(tag)) {
					type = Element.TREND;
					name = xpp.getAttributeValue(null, "name");
					if (name == null) {
						Log.e(TAG, "missing name for state based induction");
						return null;
					}
					value = xpp.getAttributeValue(null, "value");
					if (value == null) {
						Log.e(TAG, "invalid symbolic value for " + name
								+ " based induction");
						return null;
					}
				} else if ("Event".equalsIgnoreCase(tag)) {
					type = Element.EVENT;
					name = xpp.getAttributeValue(null, "name");
					if (name == null) {
						Log.e(TAG, "missing name for event based induction");
						return null;
					}
				} else if ("Ends".equalsIgnoreCase(tag)) {
					relative = xpp.getAttributeValue(null, "relativeTo");
					if (!"End".equalsIgnoreCase(relative)
							&& !"Start".equalsIgnoreCase(relative)) {
						relative = null;
					}
					try {
						gap = (new ISODuration(xpp.getAttributeValue(null,
								"gap"))).toMillis();
					} catch (InvalidDateException e) {
						Log.e(TAG, "invalid ISO Duration in induction", e);
						return null;
					}
				}
			}

		}

		if (name != null && relative != null && gap != -1 && type != -1) {
			return new Induction(type, name, context, value, range, relative,
					gap);
		}
		return null;

	}

	private void parseStates(XmlPullParser xpp) throws XmlPullParserException,
			IOException {
	//	System.out.println("******parseStates******");
		StateDef stateDef = null;
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("States")) {
			if (eventType == XmlPullParser.START_TAG
					&& "State".equals(xpp.getName())) {
				stateDef = loadState(xpp);
				if (stateDef != null) {
					_ontology.addStateDef(stateDef);
				}
			}
		}
	//	System.out.println(_ontology.printStates());

	}

	private StateDef loadState(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		int eventType;
		ArrayList<AbstractedFrom> abstractedFrom = null;
		ArrayList<String> necessaryContexts = null;
		MappingFunction mappingFunction = null;
		InterpolationFunction interpolationFunction = null;
		String name = null;

		// System.out.println("<State name="
		// + (name = xpp.getAttributeValue(null, "name")) + ">");
		name = xpp.getAttributeValue(null, "name");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("State")) {
			if (eventType == XmlPullParser.START_TAG) {

				if ("AbstractedFrom".equals(xpp.getName())) {
					abstractedFrom = parseAbstractedFrom(xpp);
				} else if ("NecessaryContexts".equals(xpp.getName())) {
					necessaryContexts = parseNecessaryContexts(xpp);

				} else if ("MappingFunction".equals(xpp.getName())) {
					mappingFunction = parseMappingFunction(xpp,abstractedFrom);

				} else if ("InterpolationFunction".equals(xpp.getName())) {
					interpolationFunction = parseInterpolationFunction(xpp);

				}
			}
		}

		if (abstractedFrom == null || necessaryContexts == null
				|| mappingFunction == null || interpolationFunction == null) {
			return null;
		}
		return new StateDef(name, abstractedFrom, necessaryContexts,
				mappingFunction, interpolationFunction);

	}

	private InterpolationFunction parseInterpolationFunction(XmlPullParser xpp)
			throws XmlPullParserException, IOException {

		int eventType;

		HashMap<String, Long> interpolationFunctionHash = new HashMap<String, Long>();

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("InterpolationFunction")) {

			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Value")) {
				// System.out.println("name="
				// + xpp.getAttributeValue(null, "name") + " maxGap="
				// + xpp.getAttributeValue(null, "maxGap") + " timeUnit="
				// + xpp.getAttributeValue(null, "timeUnit"));

				String maxGap = xpp.getAttributeValue(null, "maxGap");
				long maxGapLong;
				try {
					maxGapLong = new ISODuration(maxGap).toMillis();
				} catch (Exception e) {
					Log.e(TAG, "Corrupt maxgap value " + maxGap, e);
					return null;
				}

				interpolationFunctionHash.put(xpp.getAttributeValue(null,
						"name"), maxGapLong);
			}

		}
		if (!interpolationFunctionHash.isEmpty()) {
			return new InterpolationFunction(interpolationFunctionHash);
		}
		return null;
	}

	private ArrayList<String> parseNecessaryContexts(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		int eventType;
		ArrayList<String> necessaryContexts = new ArrayList<String>();
		// System.out.println("NecessaryContexts");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("NecessaryContexts")) {
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG
					&& tag.equalsIgnoreCase("Context")) {
				// System.out.println(xpp.getAttributeValue(null, "name"));
				String name = xpp.getAttributeValue(null, "name");
				if (!isEmpty(name)) {
					necessaryContexts.add(name);
				}

			}
		}
		if (necessaryContexts.isEmpty()) {
			return null;
		}
		return necessaryContexts;

	}

	private ArrayList<AbstractedFrom> parseAbstractedFrom(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		int eventType;
		ArrayList<AbstractedFrom> abstractedFromList = new ArrayList<AbstractedFrom>();
		int type=-1;
		// System.out.println("AbstractedFrom");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("AbstractedFrom")) {
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG) {
				if (tag.equalsIgnoreCase("Primitive")) {
					type = Element.PRIMITIVE;
				} else if (tag.equalsIgnoreCase("State")) {
					type = Element.STATE;
				} else if (tag.equalsIgnoreCase("Trend")) {
					type = Element.TREND;
				}
				// System.out.println(tag + " name="
				// + xpp.getAttributeValue(null, "name"));

				String name = xpp.getAttributeValue(null, "name");
				if (isEmpty(name) || type < 0){
					Log.e(TAG,"Missing name or type for abstractedFrom");
				}else{
					abstractedFromList.add(new AbstractedFrom(type, name));
				}

			}
		}
		if (abstractedFromList.isEmpty()) {
			return null;
		}
		return abstractedFromList;
	}

	private MappingFunction parseMappingFunction(XmlPullParser xpp,ArrayList<AbstractedFrom> abstractedFrom)
			throws XmlPullParserException, IOException {
		int eventType;
		ArrayList<MappingFunctionEntry> mappingFunction = new ArrayList<MappingFunctionEntry>();

		// System.out.println("MappingFunction");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("MappingFunction")) {
			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Value")) {

				MappingFunctionEntry mappingFunctionEntry = parseValueTag(xpp,abstractedFrom);
				if (mappingFunctionEntry == null) {
					return null;
				}
				mappingFunction.add(mappingFunctionEntry);
			}
		}
		if (mappingFunction.isEmpty()) {
			return null;
		}
		return new MappingFunction(mappingFunction);

	}

	private MappingFunctionEntry parseValueTag(XmlPullParser xpp,ArrayList<AbstractedFrom> abstractedFrom)
			throws XmlPullParserException, IOException {
		int eventType;
		AbstractedFrom isExistAf=null;
		HashMap<AbstractedFrom,ElementCondition> elementConditions = new HashMap<AbstractedFrom,ElementCondition>();

		String stateValue = xpp.getAttributeValue(null, "name");
		// System.out.println();
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Value")) {
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG) {
				
				String name = xpp.getAttributeValue(null, "name");
				if (isEmpty(name)) {
					return null;
				}
				if ("Primitive".equalsIgnoreCase(tag)) {
					isExistAf= existInAbstracedFrom(Element.PRIMITIVE,name,abstractedFrom);
					if(isExistAf==null){
						return null;
					}
					
					NumericRange numericRange = NumericRange.parseRange(xpp);
					// System.out.println("Primitive "
					// + xpp.getAttributeValue(null, "name") + " "
					// + numericRange + "\n");
					if (numericRange == null) {
						return null;
					}
					ElementCondition elementCondition = new PrimitiveCondition(
							name, numericRange);
					elementConditions.put(isExistAf,elementCondition);
				} else if ("State".equalsIgnoreCase(tag)) {
					isExistAf= existInAbstracedFrom(Element.STATE,name,abstractedFrom);
					if(isExistAf==null){
						return null;
					}
					// System.out.println("State "
					// + xpp.getAttributeValue(null, "name") + " "
					// + xpp.getAttributeValue(null, "value") + "\n");
					String elementValue = xpp.getAttributeValue(null, "value");
					if (isEmpty(elementValue)) {
						return null;
					}
					ElementCondition elementCondition = new StateCondition(
							name, elementValue);
					elementConditions.put(isExistAf,elementCondition);
				} else if ("Trend".equalsIgnoreCase(tag)) {
					// TODO implement trend entry
				}
			}
		}
		//TODO check
		if (elementConditions.isEmpty() || abstractedFrom.size()!=elementConditions.size()) {
			return null;
		}
		return new MappingFunctionEntry(stateValue, elementConditions);
	}

	private AbstractedFrom existInAbstracedFrom(int type, String name,
		ArrayList<AbstractedFrom> abstractedFrom){
		for (AbstractedFrom af : abstractedFrom){
			if(type==af.getType() && name.equalsIgnoreCase(af.getName())){
				return af;
			}
		}
		return null;
	}
}

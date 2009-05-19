package dt.processor.kbta.ontology;

import static android.text.TextUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static dt.processor.kbta.util.XmlParser.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.R;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.Patterns.BeforeTemporalCondition;
import dt.processor.kbta.ontology.defs.Patterns.BiggerValueCondition;
import dt.processor.kbta.ontology.defs.Patterns.LinearPatternDef;
import dt.processor.kbta.ontology.defs.Patterns.NoValueCondition;
import dt.processor.kbta.ontology.defs.Patterns.OverlapTemporalCondition;
import dt.processor.kbta.ontology.defs.Patterns.PairWiseCondition;
import dt.processor.kbta.ontology.defs.Patterns.PatternElementNumeric;
import dt.processor.kbta.ontology.defs.Patterns.PatternElementSymbolic;
import dt.processor.kbta.ontology.defs.Patterns.PatternElement;
import dt.processor.kbta.ontology.defs.Patterns.SameValueCondition;
import dt.processor.kbta.ontology.defs.Patterns.SmallerValueCondition;
import dt.processor.kbta.ontology.defs.Patterns.TemporalCondition;
import dt.processor.kbta.ontology.defs.Patterns.ValueCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractedFrom;
import dt.processor.kbta.ontology.defs.abstractions.state.ElementCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.InterpolationFunction;
import dt.processor.kbta.ontology.defs.abstractions.state.PrimitiveCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractionCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.state.StateMappingFunction;
import dt.processor.kbta.ontology.defs.abstractions.state.StateMappingFunctionEntry;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendMappingFunction;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.defs.context.Destruction;
import dt.processor.kbta.ontology.defs.context.EventDestruction;
import dt.processor.kbta.ontology.defs.context.EventInduction;
import dt.processor.kbta.ontology.defs.context.Induction;
import dt.processor.kbta.ontology.defs.context.PrimitiveDestruction;
import dt.processor.kbta.ontology.defs.context.PrimitiveInduction;
import dt.processor.kbta.ontology.defs.context.StateDestruction;
import dt.processor.kbta.ontology.defs.context.StateInduction;
import dt.processor.kbta.ontology.defs.context.TrendDestruction;
import dt.processor.kbta.ontology.defs.context.TrendInduction;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;
import dt.processor.kbta.util.ISODuration;

/**
 * @author
 */
public class OntologyLoader{
	public static final String TAG = "OntologyLoader";

	private final HashMap<String, PrimitiveDef> _primitives;

	private final HashMap<String, EventDef> _events;

	private final ArrayList<ContextDef> _contexts;

	private final ArrayList<StateDef> _states;

	private final ArrayList<TrendDef> _trends;

	private final ArrayList<LinearPatternDef> _patterns;

	private static final long DEFAULT_ELEMENT_TIMEOUT = 10000;

	private Long _elementTimeout;

	private static final String DEFAULT_NAME = "Android";

	private String _ontologyName;

	public OntologyLoader(){
		_primitives = new HashMap<String, PrimitiveDef>();
		_events = new HashMap<String, EventDef>();
		_contexts = new ArrayList<ContextDef>();
		_states = new ArrayList<StateDef>();
		_trends = new ArrayList<TrendDef>();
		_patterns = new ArrayList<LinearPatternDef>();
	}

	public Ontology loadOntology(Context context){
		try{
			XmlPullParser xpp = context.getResources().getXml(R.xml.ontology);

			for (int eventType = xpp.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xpp
					.next()){
				String tag;
				if (eventType != XmlPullParser.START_TAG || isEmpty(tag = xpp.getName())){
					continue;
				}

				if (tag.equalsIgnoreCase("Ontology")){
					parseOntologyTag(xpp);
				}else if (tag.equalsIgnoreCase("Primitives")){
					// parsePrimitives(xpp);
				}else if (tag.equalsIgnoreCase("Events")){
					// parseEvents(xpp);
				}else if (tag.equalsIgnoreCase("Contexts")){
					// parseContexts(xpp);
				}else if (tag.equalsIgnoreCase("States")){
					// parseStates(xpp);
				}else if (tag.equalsIgnoreCase("Trends")){
					// parseTrends(xpp);
				}else if (tag.equalsIgnoreCase("Patterns")){

					ParsePatterns(xpp);
				}
			}

			if (_elementTimeout == null){
				Log.w(TAG,
					"Missing/corrupt \"elementTimeout\" attribute in Ontology element, using default: "
							+ DEFAULT_ELEMENT_TIMEOUT);
				_elementTimeout = DEFAULT_ELEMENT_TIMEOUT;
			}

			if (isEmpty(_ontologyName)){
				Log.w(TAG,
					"Missing/corrupt \"name\" attribute in Ontology element, using default: "
							+ DEFAULT_NAME);
				_ontologyName = DEFAULT_NAME;
			}

			return new Ontology(_primitives, _events, _contexts, _states, _trends,
					_elementTimeout, _ontologyName);
		}catch(Exception e){
			Log.e(TAG, "Error while loading Ontology", e);
		}

		return null;
	}

	private void ParsePatterns(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		System.out.println("*********ParsePatterns*********");
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Patterns")){
			if (eventType == XmlPullParser.START_TAG
					&& "LinearPattern".equalsIgnoreCase(xpp.getName())){

				LinearPatternDef lpd = loadLinearPattern(xpp);
				if (lpd != null){
					_patterns.add(lpd);
				}
			}
		}
		System.out.println(Arrays.toString(_patterns.toArray()));

	}

	private LinearPatternDef loadLinearPattern(XmlPullParser xpp)
			throws XmlPullParserException, IOException{
		int eventType;
		HashMap<Integer, PatternElement> elements = new HashMap<Integer, PatternElement>();
		ArrayList<PairWiseCondition> pairWiseConditions = null;

		String patternName = xpp.getAttributeValue(null, "name");
		if (TextUtils.isEmpty(patternName)){
			Log.e(TAG, "Missing name for LinearPattern");
			return null;
		}

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("LinearPattern")){
			String name = xpp.getName();
			if (eventType == XmlPullParser.START_TAG && "Elements".equalsIgnoreCase(name)){
				elements = loadPatternElements(xpp);
				if (elements.isEmpty()){
					Log.e(TAG, "no elements to create the pattern " + patternName);
					return null;
				}

			}

			else if (eventType == XmlPullParser.START_TAG
					&& "PairWiseConditions".equalsIgnoreCase(name)){
				pairWiseConditions = parsePairWiseConditions(xpp, elements);
				if (pairWiseConditions == null){
					Log.e(TAG, "no pair wise conditions for the pattern " + patternName);
					return null;
				}

			}
		}

		return new LinearPatternDef(patternName, pairWiseConditions, elements);

	}

	private ArrayList<PairWiseCondition> parsePairWiseConditions(XmlPullParser xpp,
		HashMap<Integer, PatternElement> elements) throws XmlPullParserException,
			IOException{
		ArrayList<PairWiseCondition> pairWiseConditions = new ArrayList<PairWiseCondition>();
		int eventType;
		PairWiseCondition pairWiseCondition = null;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("PairWiseConditions")){
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG
					&& tag.equalsIgnoreCase("PairWiseCondition")){
				pairWiseCondition = parsePairWiseCondition(xpp, elements);
				if (pairWiseCondition != null){
					pairWiseConditions.add(pairWiseCondition);
				}
			}
		}

		if (pairWiseConditions.isEmpty()){
			return null;
		}

		return pairWiseConditions;
	}

	private PairWiseCondition parsePairWiseCondition(XmlPullParser xpp,
		HashMap<Integer, PatternElement> elements) throws XmlPullParserException,
			IOException{
		int first;
		int second;
		ValueCondition valueCondition = null;
		TemporalCondition temporalCondition = null;
		try{
			first = Integer.parseInt(xpp.getAttributeValue(null, "first"));
			second = Integer.parseInt(xpp.getAttributeValue(null, "second"));
			if(elements.get(first)==null || elements.get(second)==null){
				Log.e(TAG,"first/second not exist in patterns elements first="+first+" second="+second);
				return null;
			}
		}catch(NumberFormatException e){
			Log.e(TAG, "first/second must to be number");
			return null;
		}

		valueCondition = parseValuePairWiseCondition(xpp);
		temporalCondition = parseTemporalPairWiseCondition(xpp);

		if (valueCondition == null || temporalCondition == null){
			return null;
		}

		return new PairWiseCondition(first, second, valueCondition, temporalCondition);
	}

	private TemporalCondition parseTemporalPairWiseCondition(XmlPullParser xpp){
		TemporalCondition temporalCondition = null;
		String temporal = xpp.getAttributeValue(null, "temporal");
		if (TextUtils.isEmpty(temporal)){
			Log.e(TAG, "Missing temporal for pairWiseCondition");
			return null;
		}
		try{
			if (temporal.equalsIgnoreCase("Before")){
				String minGap = xpp.getAttributeValue(null, "minGap");
				if (TextUtils.isEmpty(minGap)){
					Log.e(TAG, "Missing minGap for pairWiseCondition");
					return null;
				}
				String maxGap = xpp.getAttributeValue(null, "maxGap");
				if (TextUtils.isEmpty(maxGap)){
					Log.e(TAG, "Missing maxGap for pairWiseCondition");
					return null;
				}
				temporalCondition = new BeforeTemporalCondition(new DurationCondition(
						minGap, maxGap));
			}else if (temporal.equalsIgnoreCase("Overlap")){
				String minLength = xpp.getAttributeValue(null, "minLength");
				if (TextUtils.isEmpty(minLength)){
					Log.e(TAG, "Missing minLength for pairWiseCondition");
					return null;
				}
				String maxLength = xpp.getAttributeValue(null, "maxLength");
				if (TextUtils.isEmpty(maxLength)){
					Log.e(TAG, "Missing maxLength for pairWiseCondition");
					return null;
				}
				String minStartingDistance = xpp.getAttributeValue(null,
					"minStartingDistance");
				if (TextUtils.isEmpty(minStartingDistance)){
					Log.e(TAG, "Missing minStartingDistance for pairWiseCondition");
					return null;
				}
				String maxStartingDistance = xpp.getAttributeValue(null,
					"maxStartingDistance");
				if (TextUtils.isEmpty(maxStartingDistance)){
					Log.e(TAG, "Missing maxStartingDistance for pairWiseCondition");
					return null;
				}
				temporalCondition = new OverlapTemporalCondition(new DurationCondition(
						minLength, maxLength), new DurationCondition(minStartingDistance,
						maxStartingDistance));
			}

		}catch(Exception e){
			Log.e(TAG, "Improper duration in PairWiseCondition", e);
			return null;
		}
		if (temporalCondition == null){
			return null;
		}
		return temporalCondition;
	}

	private ValueCondition parseValuePairWiseCondition(XmlPullParser xpp){
		ValueCondition valueCondition = null;
		String value = xpp.getAttributeValue(null, "value");
		if (TextUtils.isEmpty(value)){
			Log.e(TAG, "Missing value for pairWiseCondition");
			return null;
		}
		if (value.equalsIgnoreCase("*")){
			valueCondition = new NoValueCondition();
		}else if (value.equalsIgnoreCase("Same")){
			valueCondition = new SameValueCondition();
		}else if (value.equalsIgnoreCase("Smaller")){
			valueCondition = new SmallerValueCondition();
		}else if (value.equalsIgnoreCase("Bigger")){
			valueCondition = new BiggerValueCondition();
		}else{
			Log.e(TAG, "Corrupt value of pairWiseCondition");
			return null;
		}

		return valueCondition;
	}

	private HashMap<Integer, PatternElement> loadPatternElements(XmlPullParser xpp)
			throws NumberFormatException, XmlPullParserException, IOException{
		int eventType;
		HashMap<Integer, PatternElement> elements = new HashMap<Integer, PatternElement>();
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Elements")){

			if (eventType == XmlPullParser.START_TAG){
				String stringTypeElement = xpp.getName();

				if ("Primitive".equalsIgnoreCase(stringTypeElement)
						|| "Event".equalsIgnoreCase(stringTypeElement)
						|| "Trend".equalsIgnoreCase(stringTypeElement)
						|| "State".equalsIgnoreCase(stringTypeElement)
						|| "Context".equalsIgnoreCase(stringTypeElement)){
					PatternElement patternElement = null;

					int ordinal = Integer
							.parseInt(xpp.getAttributeValue(null, "ordinal"));
					String nameElement = xpp.getAttributeValue(null, "name");
					if (TextUtils.isEmpty(nameElement)){
						Log.e(TAG, "Missing name for nameElement");
						return null;
					}

					if ("Primitive".equalsIgnoreCase(stringTypeElement)){
						patternElement = parseElementNumeric(Element.PRIMITIVE, xpp,
							nameElement, "Primitive", ordinal);
					}else if ("Event".equalsIgnoreCase(stringTypeElement)){
						patternElement = parseElement(Element.EVENT, xpp, nameElement,
							"Event", ordinal);
					}else if ("Trend".equalsIgnoreCase(stringTypeElement)){
						patternElement = parseElementSymbolic(Element.TREND, xpp,
							nameElement, "Trend", ordinal);
					}else if ("State".equalsIgnoreCase(stringTypeElement)){
						patternElement = parseElementSymbolic(Element.STATE, xpp,
							nameElement, "State", ordinal);
					}else if ("Context".equalsIgnoreCase(stringTypeElement)){
						patternElement = parseElement(Element.CONTEXT, xpp, nameElement,
							"Context", ordinal);
					}

					if (patternElement != null){
						elements.put(ordinal, patternElement);
					}

				}else{
					Log.e(TAG, "invalid pattern element");
					continue;
				}

			}
		}

		return elements;
	}

	private PatternElementNumeric parseElementNumeric(int type, XmlPullParser xpp,
		String nameElement, String elementTypeName, int ordinal)
			throws XmlPullParserException, IOException{

		NumericRange numericRange = null;
		String name = null;
		DurationCondition durationCondition = null;
		int eventType;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase(elementTypeName)){
			if (eventType == XmlPullParser.START_TAG){
				name = xpp.getName();
				if ("NumericValueConditon".equalsIgnoreCase(name)){
					numericRange = parseNumericRange(xpp);

				}else if ("DurationCondition".equalsIgnoreCase(name)){
					durationCondition = parseDurationCondition(xpp);
				}

			}

		}

		if (durationCondition == null || numericRange == null){
			Log.e(TAG, "Invalid numeric pattern element");
			return null;
		}
		return new PatternElementNumeric(type, nameElement, ordinal, durationCondition,
				numericRange);

	}

	private PatternElementSymbolic parseElementSymbolic(int type, XmlPullParser xpp,
		String nameElement, String elementTypeName, int ordinal)
			throws XmlPullParserException, IOException{

		SymbolicValueCondition symbolicValueCondition = null;
		String name = null;
		DurationCondition durationCondition = null;
		int eventType;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase(elementTypeName)){
			if (eventType == XmlPullParser.START_TAG){
				name = xpp.getName();
				if ("SymbolicValueCondition".equalsIgnoreCase(name)){
					symbolicValueCondition = parseSymbolicValueCondition(xpp);

				}else if ("DurationCondition".equalsIgnoreCase(name)){
					durationCondition = parseDurationCondition(xpp);
				}

			}

		}

		if (durationCondition == null || symbolicValueCondition == null){
			Log.e(TAG, "Invalid numeric pattern element");
			return null;
		}
		return new PatternElementSymbolic(type, nameElement, ordinal, durationCondition,
				symbolicValueCondition);
	}

	private PatternElement parseElement(int type, XmlPullParser xpp, String nameElement,
		String elementTypeName, int ordinal) throws XmlPullParserException, IOException{

		String name = null;
		DurationCondition durationCondition = null;
		int eventType;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase(elementTypeName)){
			if (eventType == XmlPullParser.START_TAG){
				name = xpp.getName();
				if ("DurationCondition".equalsIgnoreCase(name)){
					durationCondition = parseDurationCondition(xpp);
				}

			}

		}

		if (durationCondition == null){
			Log.e(TAG, "Invalid numeric pattern element");
			return null;
		}
		return new PatternElement(type, nameElement, ordinal, durationCondition);

	}

	private void parseTrends(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Trends")){
			if (eventType == XmlPullParser.START_TAG
					&& "Trend".equalsIgnoreCase(xpp.getName())){

				TrendDef trendDef = loadTrend(xpp);
				if (trendDef != null){
					_trends.add(trendDef);
				}
			}
		}

	}

	private TrendDef loadTrend(XmlPullParser xpp) throws XmlPullParserException,
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
					necessaryContexts = parseNecessaryContexts(xpp);
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

	private void parseOntologyTag(XmlPullParser xpp){
		_ontologyName = xpp.getAttributeValue(null, "name");

		try{
			long elementTimeout = new ISODuration(xpp.getAttributeValue(null,
				"elementTimeout")).toMillis();
			if (elementTimeout > 1000){
				_elementTimeout = elementTimeout;
			}else{
				Log.w(TAG, "Element timeout must be at least 1 second");
			}
		}catch(Exception e){
			Log.w(TAG,
				"Missing/corrupt \"elementTimeout\" attribute in Ontology element", e);
		}
	}

	private void parsePrimitives(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		for (int eventType = xpp.next(); eventType != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Primitives"); eventType = xpp.next()){
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG && tag.equalsIgnoreCase("Primitive")){
				String name = xpp.getAttributeValue(null, "name");
				if (TextUtils.isEmpty(name)){
					Log.e(TAG, "Missing name for a primitive");
					continue;
				}
				NumericRange range = parseNumericRange(xpp);
				if (range == null){
					Log.e(TAG, "Invalid numeric range for the primitive " + name);
					continue;
				}
				PrimitiveDef pd = new PrimitiveDef(name, range);
				_primitives.put(pd.getName(), pd);
			}
		}
	}

	private void parseEvents(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		for (int eventType = xpp.next(); eventType != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Events"); eventType = xpp.next()){

			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Event")){
				String name = xpp.getAttributeValue(null, "name");
				if (TextUtils.isEmpty(name)){
					Log.e(TAG, "Missing name for an event");
					continue;
				}
				EventDef ed = new EventDef(name);
				_events.put(ed.getName(), ed);
			}
		}
	}

	private void parseContexts(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Contexts")){
			if (eventType == XmlPullParser.START_TAG
					&& "Context".equalsIgnoreCase(xpp.getName())){
				loadContext(xpp);
			}
		}
	}

	private void loadContext(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		ArrayList<Induction> inductions = null;
		ArrayList<Destruction> destructions = null;

		String contextName = xpp.getAttributeValue(null, "name");
		if (contextName == null){
			Log.e(TAG, "No name for context");
			return;
		}

		// Parsing inductions and destructions
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Context")){
			if (eventType == XmlPullParser.START_TAG){
				String tag = xpp.getName();
				if ("Inductions".equalsIgnoreCase(tag)){
					inductions = parseInductions(xpp, contextName);
				}else if ("Destructions".equalsIgnoreCase(tag)){
					destructions = parseDestructions(xpp, contextName);
				}
			}

		}
		if (inductions == null || inductions.isEmpty()){
			Log.e(TAG, "No inductions for context: " + contextName);
			return;
		}

		ContextDef cd = new ContextDef(contextName, inductions, destructions);
		_contexts.add(cd);
	}

	private ArrayList<Destruction> parseDestructions(XmlPullParser xpp, String contextName)
			throws XmlPullParserException, IOException{
		ArrayList<Destruction> destructions = new ArrayList<Destruction>();
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Destructions")){
			if (eventType == XmlPullParser.START_TAG){
				Destruction destruction = parseDestructionElement(xpp, contextName, xpp
						.getName());
				if (destruction != null){
					destructions.add(destruction);
				}
			}
		}
		return destructions;
	}

	private Destruction parseDestructionElement(XmlPullParser xpp, String contextName,
		String name){
		Destruction destruction = null;
		String elementName = xpp.getAttributeValue(null, "name");
		if (elementName == null){
			Log.e(TAG, "Missing name for a " + name + " based Destruction");
			return null;
		}

		if ("Primitive".equalsIgnoreCase(name)){
			NumericRange range = parseNumericRange(xpp);
			if (range == null){
				Log.e(TAG, "Invalid numeric range for a " + name + " based Destruction");
				return null;
			}
			destruction = new PrimitiveDestruction(elementName, contextName, range);
		}else if ("State".equalsIgnoreCase(name)){
			String value = xpp.getAttributeValue(null, "value");
			if (value == null){
				Log.e(TAG, "Invalid symbolic value for a " + name + " based Destruction");
				return null;
			}
			destruction = new StateDestruction(elementName, contextName, value);
		}else if ("Trend".equalsIgnoreCase(name)){
			String value = xpp.getAttributeValue(null, "value");
			if (value == null){
				Log.e(TAG, "Invalid symbolic value for a " + name + " based Destruction");
				return null;
			}
			destruction = new TrendDestruction(elementName, contextName, value);
		}else if ("Event".equalsIgnoreCase(name)){
			destruction = new EventDestruction(elementName, contextName);

		}else{
			Log.e(TAG, "Unsupported element type (" + name + ") at context: "
					+ contextName);
			return null;
		}
		return destruction;
	}

	private ArrayList<Induction> parseInductions(XmlPullParser xpp, String contextName)
			throws XmlPullParserException, IOException{
		ArrayList<Induction> inductions = new ArrayList<Induction>();
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Inductions")){
			if (eventType == XmlPullParser.START_TAG
					&& "Induction".equalsIgnoreCase(xpp.getName())){
				Induction induction = loadInduction(xpp, contextName);
				if (induction != null){
					inductions.add(induction);
				}
			}
		}
		return inductions;
	}

	private Induction loadInduction(XmlPullParser xpp, String contextName)
			throws XmlPullParserException, IOException{
		Long gap = null;
		Boolean relativeToStart = null;
		int eventType;
		Induction induction = null;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Induction")){
			if (eventType == XmlPullParser.START_TAG){
				String tag = xpp.getName();

				// Parsing the ends tag
				if ("Ends".equalsIgnoreCase(tag)){
					try{
						gap = new ISODuration(xpp.getAttributeValue(null, "gap"))
								.toMillis();
					}catch(Exception e){
						Log.e(TAG,
							"Invalid \"ends\" tag (gap attribute) in induction for context: "
									+ contextName, e);
						return null;
					}

					String relativeTo = xpp.getAttributeValue(null, "relativeTo");
					if ("End".equalsIgnoreCase(relativeTo)){
						relativeToStart = false;
					}else if ("Start".equalsIgnoreCase(relativeTo)){
						relativeToStart = true;
					}else{
						Log.e(TAG,
							"Invalid \"ends\" tag (relativeTo attribute) in induction for context: "
									+ contextName);
						return null;
					}
				}else{
					// Parsing the element tag
					if ((induction = parseInductionElement(xpp, contextName, tag)) == null){
						return null;
					}
				}
			}
		}

		if (induction == null || gap == null || relativeToStart == null){
			return null;
		}

		return induction.setRelativeToAndGap(relativeToStart, gap);
	}

	/**
	 * Parses the element tag of the induction and instantiates the proper induction type
	 * according to the element type
	 * 
	 * @return The instantiated induction or null if the tag is not a supported element or
	 *         the element is corrupt
	 */
	private Induction parseInductionElement(XmlPullParser xpp, String contextName,
		String tag){
		Induction induction = null;
		String elementName = xpp.getAttributeValue(null, "name");
		if (elementName == null){
			Log.e(TAG, "Missing name for a " + tag + " based induction");
			return null;
		}

		if ("Primitive".equalsIgnoreCase(tag)){
			NumericRange range = parseNumericRange(xpp);
			if (range == null){
				Log.e(TAG, "Invalid numeric range for a " + tag + " based induction");
				return null;
			}
			induction = new PrimitiveInduction(elementName, contextName, range);
		}else if ("State".equalsIgnoreCase(tag)){
			String value = xpp.getAttributeValue(null, "value");
			if (value == null){
				Log.e(TAG, "Invalid symbolic value for a " + tag + " based induction");
				return null;
			}
			induction = new StateInduction(elementName, contextName, value);
		}else if ("Trend".equalsIgnoreCase(tag)){
			String value = xpp.getAttributeValue(null, "value");
			if (value == null){
				Log.e(TAG, "Invalid symbolic value for a " + tag + " based induction");
				return null;
			}
			induction = new TrendInduction(elementName, contextName, value);
		}else if ("Event".equalsIgnoreCase(tag)){
			induction = new EventInduction(elementName, contextName);

		}else{
			Log.e(TAG, "Unsupported element type (" + tag + ") at context: "
					+ contextName);
			return null;
		}
		return induction;
	}

	private void parseStates(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("States")){
			if (eventType == XmlPullParser.START_TAG
					&& "State".equalsIgnoreCase(xpp.getName())){
				StateDef stateDef = loadState(xpp);
				if (stateDef != null){
					_states.add(stateDef);
				}
			}
		}
	}

	private StateDef loadState(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;
		ArrayList<AbstractedFrom> abstractedFrom = null;
		ArrayList<String> necessaryContexts = null;
		StateMappingFunction mappingFunction = null;
		InterpolationFunction interpolationFunction = null;
		String name = null;

		name = xpp.getAttributeValue(null, "name");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("State")){
			if (eventType == XmlPullParser.START_TAG){
				if ("AbstractedFrom".equalsIgnoreCase(xpp.getName())){
					abstractedFrom = parseAbstractedFrom(xpp);
				}else if ("NecessaryContexts".equalsIgnoreCase(xpp.getName())){
					necessaryContexts = parseNecessaryContexts(xpp);
				}else if ("MappingFunction".equalsIgnoreCase(xpp.getName())){
					mappingFunction = parseMappingFunction(xpp, abstractedFrom);
				}else if ("InterpolationFunction".equalsIgnoreCase(xpp.getName())){
					interpolationFunction = parseInterpolationFunction(xpp);
				}
			}
		}

		if (abstractedFrom == null || necessaryContexts == null
				|| mappingFunction == null || interpolationFunction == null){
			return null;
		}
		return new StateDef(name, abstractedFrom, necessaryContexts, mappingFunction,
				interpolationFunction);

	}

	private InterpolationFunction parseInterpolationFunction(XmlPullParser xpp)
			throws XmlPullParserException, IOException{
		int eventType;
		HashMap<String, Long> interpolationFunctionHash = new HashMap<String, Long>();

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("InterpolationFunction")){

			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Value")){

				String maxGap = xpp.getAttributeValue(null, "maxGap");
				long maxGapLong;
				try{
					maxGapLong = new ISODuration(maxGap).toMillis();
				}catch(Exception e){
					Log.e(TAG, "Corrupt maxgap value " + maxGap, e);
					return null;
				}

				interpolationFunctionHash.put(xpp.getAttributeValue(null, "name"),
					maxGapLong);
			}

		}
		if (!interpolationFunctionHash.isEmpty()){
			return new InterpolationFunction(interpolationFunctionHash);
		}
		return null;
	}

	private ArrayList<String> parseNecessaryContexts(XmlPullParser xpp)
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
		if (necessaryContexts.isEmpty()){
			return null;
		}
		return necessaryContexts;

	}

	private ArrayList<AbstractedFrom> parseAbstractedFrom(XmlPullParser xpp)
			throws XmlPullParserException, IOException{
		int eventType;
		ArrayList<AbstractedFrom> abstractedFromList = new ArrayList<AbstractedFrom>();
		int type = -1;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("AbstractedFrom")){
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG){
				if (tag.equalsIgnoreCase("Primitive")){
					type = Element.PRIMITIVE;
				}else if (tag.equalsIgnoreCase("State")){
					type = Element.STATE;
				}else if (tag.equalsIgnoreCase("Trend")){
					type = Element.TREND;
				}

				String name = xpp.getAttributeValue(null, "name");
				if (isEmpty(name) || type < 0){
					Log.e(TAG, "Missing name or type for abstractedFrom");
				}else{
					abstractedFromList.add(new AbstractedFrom(type, name));
				}

			}
		}
		if (abstractedFromList.isEmpty()){
			return null;
		}
		return abstractedFromList;
	}

	private StateMappingFunction parseMappingFunction(XmlPullParser xpp,
		ArrayList<AbstractedFrom> abstractedFrom) throws XmlPullParserException,
			IOException{
		int eventType;
		ArrayList<StateMappingFunctionEntry> mappingFunction = new ArrayList<StateMappingFunctionEntry>();

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("MappingFunction")){
			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Value")){

				StateMappingFunctionEntry mappingFunctionEntry = parseValueTag(xpp,
					abstractedFrom);
				if (mappingFunctionEntry == null){
					return null;
				}
				mappingFunction.add(mappingFunctionEntry);
			}
		}
		if (mappingFunction.isEmpty()){
			return null;
		}
		return new StateMappingFunction(mappingFunction);

	}

	private StateMappingFunctionEntry parseValueTag(XmlPullParser xpp,
		ArrayList<AbstractedFrom> abstractedFrom) throws XmlPullParserException,
			IOException{
		int eventType;
		AbstractedFrom isExistAf = null;
		HashMap<AbstractedFrom, ElementCondition> elementConditions = new HashMap<AbstractedFrom, ElementCondition>();

		String stateValue = xpp.getAttributeValue(null, "name");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Value")){
			String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG){

				String name = xpp.getAttributeValue(null, "name");
				if (isEmpty(name)){
					return null;
				}
				if ("Primitive".equalsIgnoreCase(tag)){
					isExistAf = existsInAbstracedFrom(Element.PRIMITIVE, name,
						abstractedFrom);
					if (isExistAf == null){
						return null;
					}

					NumericRange numericRange = parseNumericRange(xpp);

					if (numericRange == null){
						return null;
					}
					ElementCondition elementCondition = new PrimitiveCondition(name,
							numericRange);
					elementConditions.put(isExistAf, elementCondition);
				}else if ("State".equalsIgnoreCase(tag) || "Trend".equalsIgnoreCase(tag)){
					int type = "State".equalsIgnoreCase(tag) ? Element.STATE
							: Element.TREND;
					isExistAf = existsInAbstracedFrom(type, name, abstractedFrom);
					if (isExistAf == null){
						return null;
					}

					String elementValue = xpp.getAttributeValue(null, "value");
					if (isEmpty(elementValue)){
						return null;
					}
					ElementCondition elementCondition = new AbstractionCondition(name,
							elementValue);
					elementConditions.put(isExistAf, elementCondition);
				}
			}
		}

		if (elementConditions.isEmpty()
				|| abstractedFrom.size() != elementConditions.size()){
			return null;
		}
		return new StateMappingFunctionEntry(stateValue, elementConditions);
	}

	private AbstractedFrom existsInAbstracedFrom(int type, String name,
		ArrayList<AbstractedFrom> abstractedFrom){
		for (AbstractedFrom af : abstractedFrom){
			if (type == af.getType() && name.equalsIgnoreCase(af.getName())){
				return af;
			}
		}
		return null;
	}

	private NumericRange parseNumericRange(XmlPullParser xpp){
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
}

package dt.processor.kbta.ontology;

import static android.text.TextUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.R;
import dt.processor.kbta.ontology.defs.EventDef;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractedFrom;
import dt.processor.kbta.ontology.defs.abstractions.state.ElementCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.InterpolationFunction;
import dt.processor.kbta.ontology.defs.abstractions.state.MappingFunction;
import dt.processor.kbta.ontology.defs.abstractions.state.MappingFunctionEntry;
import dt.processor.kbta.ontology.defs.abstractions.state.PrimitiveCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.StateCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.trend.InterpolateMappingFunction;
import dt.processor.kbta.ontology.defs.abstractions.trend.TrendDef;
import dt.processor.kbta.ontology.defs.context.ContextDef;
import dt.processor.kbta.ontology.defs.context.Destruction;
import dt.processor.kbta.ontology.defs.context.EventInduction;
import dt.processor.kbta.ontology.defs.context.Induction;
import dt.processor.kbta.ontology.defs.context.PrimitiveInduction;
import dt.processor.kbta.ontology.defs.context.StateInduction;
import dt.processor.kbta.ontology.defs.context.TrendInduction;
import dt.processor.kbta.ontology.instances.Element;
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
					parsePrimitives(xpp);
				}else if (tag.equalsIgnoreCase("Events")){
					parseEvents(xpp);
				}else if (tag.equalsIgnoreCase("Contexts")){
					parseContexts(xpp);
				}else if (tag.equalsIgnoreCase("States")){
					parseStates(xpp);
				}else if (tag.equalsIgnoreCase("Trends")){
					parseTrends(xpp);
				}else if (tag.equalsIgnoreCase("Patterns")){
					// TODO Parse patterns
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

			return new Ontology(_primitives, _events, _contexts, _states,_trends,
					_elementTimeout, _ontologyName);
		}catch(Exception e){
			Log.e(TAG, "Error while loading Ontology", e);
		}

		return null;
	}

	private void parseTrends(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		System.out.println("******parseTrends******");
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Trends")){
			if (eventType == XmlPullParser.START_TAG && "Trend".equals(xpp.getName())){
				
				TrendDef trendDef = loadTrend(xpp);
				if (trendDef != null){
					_trends.add(trendDef);
				}
			}
		}
		System.out.println(_trends);
	}

	private TrendDef loadTrend(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;
		String abstractedFrom = null;
		ArrayList<String> necessaryContexts = null;
		InterpolateMappingFunction mappingFunction = null;
		String name = null;

		name = xpp.getAttributeValue(null, "name");
		if (TextUtils.isEmpty(name)){
			Log.e(TAG, "Missing name for trend");
			return null;
		}

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Trend")){
			if (eventType == XmlPullParser.START_TAG){
				if ("AbstractedFrom".equals(xpp.getName())){
					abstractedFrom = xpp.getAttributeValue(null, "name");
					if (TextUtils.isEmpty(abstractedFrom)){
						Log.e(TAG, "Missing name for an primitive");
						return null;
					}
				}else if ("NecessaryContexts".equals(xpp.getName())){
					necessaryContexts = parseNecessaryContexts(xpp);
				}else if ("MappingFunction".equals(xpp.getName())){
					Double treshold=null;
					Double angle=null;
					Long maxGap=null;
					try{
						treshold = Double.parseDouble(xpp.getAttributeValue(null,
							"treshold"));
						angle = Double.parseDouble(xpp.getAttributeValue(null, "angle"));
					}catch(NumberFormatException e){
						Log.e(TAG, "Corrupt treshold or angle " + treshold + " " + angle,
							e);
						return null;
					}

					try{
						maxGap = new ISODuration(xpp.getAttributeValue(null, "maxGap"))
								.toMillis();
					}catch(Exception e){
						Log.e(TAG, "Corrupt maxgap value " + maxGap, e);
						return null;
					}
					
					mappingFunction = new InterpolateMappingFunction(treshold,angle,maxGap);
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
			if (eventType == XmlPullParser.START_TAG && "Context".equals(xpp.getName())){
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
					// TODO parse destructions
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

	private ArrayList<Induction> parseInductions(XmlPullParser xpp, String contextName)
			throws XmlPullParserException, IOException{
		ArrayList<Induction> inductions = new ArrayList<Induction>();
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Inductions")){
			if (eventType == XmlPullParser.START_TAG && "Induction".equals(xpp.getName())){
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
			if (eventType == XmlPullParser.START_TAG && "State".equals(xpp.getName())){
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
		MappingFunction mappingFunction = null;
		InterpolationFunction interpolationFunction = null;
		String name = null;

	
		name = xpp.getAttributeValue(null, "name");
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("State")){
			if (eventType == XmlPullParser.START_TAG){
				if ("AbstractedFrom".equals(xpp.getName())){
					abstractedFrom = parseAbstractedFrom(xpp);
				}else if ("NecessaryContexts".equals(xpp.getName())){
					necessaryContexts = parseNecessaryContexts(xpp);
				}else if ("MappingFunction".equals(xpp.getName())){
					mappingFunction = parseMappingFunction(xpp, abstractedFrom);
				}else if ("InterpolationFunction".equals(xpp.getName())){
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

	private MappingFunction parseMappingFunction(XmlPullParser xpp,
		ArrayList<AbstractedFrom> abstractedFrom) throws XmlPullParserException,
			IOException{
		int eventType;
		ArrayList<MappingFunctionEntry> mappingFunction = new ArrayList<MappingFunctionEntry>();

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("MappingFunction")){
			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("Value")){

				MappingFunctionEntry mappingFunctionEntry = parseValueTag(xpp,
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
		return new MappingFunction(mappingFunction);

	}

	private MappingFunctionEntry parseValueTag(XmlPullParser xpp,
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
				}else if ("State".equalsIgnoreCase(tag)){
					isExistAf = existsInAbstracedFrom(Element.STATE, name, abstractedFrom);
					if (isExistAf == null){
						return null;
					}
				
					String elementValue = xpp.getAttributeValue(null, "value");
					if (isEmpty(elementValue)){
						return null;
					}
					ElementCondition elementCondition = new StateCondition(name,
							elementValue);
					elementConditions.put(isExistAf, elementCondition);
				}else if ("Trend".equalsIgnoreCase(tag)){
					// TODO implement trend entry
				}
			}
		}

		if (elementConditions.isEmpty()
				|| abstractedFrom.size() != elementConditions.size()){
			return null;
		}
		return new MappingFunctionEntry(stateValue, elementConditions);
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
		if ("*".equals(minS)){
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
		if ("*".equals(maxS)){
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

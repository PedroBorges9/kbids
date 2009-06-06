package dt.processor.kbta.ontology.loader;

import static android.text.TextUtils.isEmpty;
import static dt.processor.kbta.ontology.loader.OntologyLoader.TAG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractedFrom;
import dt.processor.kbta.ontology.defs.abstractions.state.AbstractionCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.ElementCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.InterpolationFunction;
import dt.processor.kbta.ontology.defs.abstractions.state.PrimitiveCondition;
import dt.processor.kbta.ontology.defs.abstractions.state.StateDef;
import dt.processor.kbta.ontology.defs.abstractions.state.StateMappingFunction;
import dt.processor.kbta.ontology.defs.abstractions.state.StateMappingFunctionEntry;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.XmlParser;

public class StateLoader{
	private final ArrayList<StateDef> _states;

	public StateLoader(ArrayList<StateDef> states){
		this._states = states;
	}

	public void parseStates(XmlPullParser xpp) throws XmlPullParserException, IOException{
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("States")){
			if (eventType == XmlPullParser.START_TAG
					&& "State".equalsIgnoreCase(xpp.getName())){
				StateDef stateDef = parseState(xpp);
				if (stateDef != null){
					_states.add(stateDef);
				}
			}
		}
	}

	private StateDef parseState(XmlPullParser xpp) throws XmlPullParserException,
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
					necessaryContexts = XmlParser.parseNecessaryContexts(xpp);
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

					NumericRange numericRange = XmlParser.parseNumericRange(xpp, TAG);

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
}

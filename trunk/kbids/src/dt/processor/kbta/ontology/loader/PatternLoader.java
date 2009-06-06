package dt.processor.kbta.ontology.loader;

import static dt.processor.kbta.ontology.loader.OntologyLoader.TAG;
import static dt.processor.kbta.util.XmlParser.parseSymbolicValueCondition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.patterns.BeforeTemporalCondition;
import dt.processor.kbta.ontology.defs.patterns.LinearPatternDef;
import dt.processor.kbta.ontology.defs.patterns.OverlapTemporalCondition;
import dt.processor.kbta.ontology.defs.patterns.PairWiseCondition;
import dt.processor.kbta.ontology.defs.patterns.TemporalCondition;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElement;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElementContext;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElementEvent;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElementPrimitive;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElementState;
import dt.processor.kbta.ontology.defs.patterns.patternElements.PatternElementTrend;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.threats.DurationCondition;
import dt.processor.kbta.threats.SymbolicValueCondition;
import dt.processor.kbta.util.XmlParser;

public class PatternLoader{
	private final ArrayList<LinearPatternDef> _patterns;

	public PatternLoader(ArrayList<LinearPatternDef> patterns){
		_patterns = patterns;
	}

	public void parsePatterns(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;
		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Patterns")){
			if (eventType == XmlPullParser.START_TAG
					&& "LinearPattern".equalsIgnoreCase(xpp.getName())){

				LinearPatternDef lpd = parseLinearPattern(xpp);
				if (lpd != null){
					_patterns.add(lpd);
				}
			}
		}
	}

	private LinearPatternDef parseLinearPattern(XmlPullParser xpp)
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
				elements = parsePatternElements(xpp);
				if (elements.isEmpty()){
					Log.e(TAG, "No elements to create the pattern " + patternName);
					return null;
				}
			}

			else if (eventType == XmlPullParser.START_TAG
					&& "PairWiseConditions".equalsIgnoreCase(name)){
				pairWiseConditions = parsePairWiseConditions(xpp, elements);
				if (pairWiseConditions == null){
					Log.e(TAG, "No valid pair wise conditions for the pattern "
							+ patternName);
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
				if (pairWiseCondition == null){
					return null;
				}
				pairWiseConditions.add(pairWiseCondition);
			}
		}

		return pairWiseConditions;
	}

	private PairWiseCondition parsePairWiseCondition(XmlPullParser xpp,
		HashMap<Integer, PatternElement> elements) throws XmlPullParserException,
			IOException{
		int first;
		int second;
		boolean comparable = false;
		Integer valueCondition = null;
		TemporalCondition temporalCondition = null;
		try{
			first = Integer.parseInt(xpp.getAttributeValue(null, "first"));
			second = Integer.parseInt(xpp.getAttributeValue(null, "second"));
			PatternElement firstElement = elements.get(first);
			PatternElement secondElement = elements.get(second);
			if (firstElement == null || secondElement == null){
				Log.e(TAG, "first/second not exist in patterns elements first=" + first
						+ " second=" + second);
				return null;
			}
			int comparedType = firstElement.getType();
			comparable = (comparedType == secondElement.getType())
					&& (comparedType == Element.PRIMITIVE || comparedType == Element.STATE);
		}catch(NumberFormatException e){
			Log.e(TAG, "first/second must to be number");
			return null;
		}

		valueCondition = parseValuePairWiseCondition(xpp, comparable);
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

	private Integer parseValuePairWiseCondition(XmlPullParser xpp, boolean comparable){
		Integer valueCondition = null;
		String value = xpp.getAttributeValue(null, "value");
		if (TextUtils.isEmpty(value)){
			Log.e(TAG, "Missing value for pairWiseCondition");
			return null;
		}
		if (value.equalsIgnoreCase("*")){
			valueCondition = PairWiseCondition.DONTCARE;
		}else if (value.equalsIgnoreCase("Same") && comparable){
			valueCondition = PairWiseCondition.SAME;
		}else if (value.equalsIgnoreCase("Smaller") && comparable){
			valueCondition = PairWiseCondition.SMALLER;
		}else if (value.equalsIgnoreCase("Bigger") && comparable){
			valueCondition = PairWiseCondition.BIGGER;
		}else{
			Log.e(TAG, "Corrupt value of pairWiseCondition");
			return null;
		}

		return valueCondition;
	}

	private HashMap<Integer, PatternElement> parsePatternElements(XmlPullParser xpp)
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
						patternElement = parseElementPrimitive(xpp, nameElement,
							"Primitive", ordinal);
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
					Log.e(TAG, "Invalid pattern element: " + stringTypeElement);
					continue;
				}
			}
		}

		return elements;
	}

	private PatternElementPrimitive parseElementPrimitive(XmlPullParser xpp,
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
					numericRange = XmlParser.parseNumericRange(xpp, TAG);

				}else if ("DurationCondition".equalsIgnoreCase(name)){
					durationCondition = XmlParser.parseDurationCondition(xpp, TAG);
				}
			}
		}

		if (/* durationCondition == null || */numericRange == null){
			Log.e(TAG, "Invalid " + elementTypeName + " pattern element");
			return null;
		}
		return new PatternElementPrimitive(Element.PRIMITIVE, nameElement, ordinal,
				durationCondition, numericRange);
	}

	private PatternElement parseElementSymbolic(int type, XmlPullParser xpp,
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
					durationCondition = XmlParser.parseDurationCondition(xpp, TAG);
				}
			}
		}

		if (/* durationCondition == null || */symbolicValueCondition == null){
			Log.e(TAG, "Invalid " + elementTypeName + " pattern element");
			return null;
		}
		if (type == Element.STATE){
			return new PatternElementState(type, nameElement, ordinal, durationCondition,
					symbolicValueCondition);
		}else if (type == Element.TREND){
			return new PatternElementTrend(type, nameElement, ordinal, durationCondition,
					symbolicValueCondition);
		}else{
			return null;
		}
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
					durationCondition = XmlParser.parseDurationCondition(xpp, TAG);
				}
			}
		}

		// if (durationCondition == null){
		// Log.e(TAG, "Invalid " + elementTypeName + " pattern element");
		// return null;
		// }
		if (type == Element.CONTEXT){
			return new PatternElementContext(type, nameElement, ordinal,
					durationCondition);
		}else if (type == Element.EVENT){
			return new PatternElementEvent(type, nameElement, ordinal, durationCondition);
		}else{
			return null;
		}

	}
}

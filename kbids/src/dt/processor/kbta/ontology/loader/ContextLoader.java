package dt.processor.kbta.ontology.loader;

import static dt.processor.kbta.ontology.loader.OntologyLoader.TAG;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import dt.processor.kbta.ontology.defs.NumericRange;
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
import dt.processor.kbta.util.ISODuration;
import dt.processor.kbta.util.XmlParser;

public class ContextLoader{
	private final ArrayList<ContextDef> _contexts;

	public ContextLoader(ArrayList<ContextDef> contexts){
		_contexts = contexts;
	}

	public void parseContexts(XmlPullParser xpp) throws XmlPullParserException,
			IOException{
		int eventType;

		while ((eventType = xpp.next()) != XmlPullParser.END_TAG
				|| !xpp.getName().equalsIgnoreCase("Contexts")){
			if (eventType == XmlPullParser.START_TAG
					&& "Context".equalsIgnoreCase(xpp.getName())){
				parseContext(xpp);
			}
		}
	}

	private void parseContext(XmlPullParser xpp) throws XmlPullParserException,
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
			NumericRange range = XmlParser.parseNumericRange(xpp, TAG);
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
				Induction induction = parseInduction(xpp, contextName);
				if (induction != null){
					inductions.add(induction);
				}
			}
		}
		return inductions;
	}

	private Induction parseInduction(XmlPullParser xpp, String contextName)
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
						String gapStr = xpp.getAttributeValue(null, "gap");
						if ("*".equals(gapStr)){
							gap = Long.MAX_VALUE;
						}else{
							gap = new ISODuration(gapStr)
							.toMillis();
						}						
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
			NumericRange range = XmlParser.parseNumericRange(xpp, TAG);
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
}

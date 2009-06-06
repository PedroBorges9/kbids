package dt.processor.kbta.ontology.loader;

import static dt.processor.kbta.ontology.loader.OntologyLoader.TAG;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Log;
import dt.processor.kbta.ontology.defs.NumericRange;
import dt.processor.kbta.ontology.defs.PrimitiveDef;
import dt.processor.kbta.util.XmlParser;

public class PrimitiveLoader{
	private final HashMap<String, PrimitiveDef> _primitives;

	public PrimitiveLoader(HashMap<String, PrimitiveDef> primitives){
		_primitives = primitives;
	}

	public void parsePrimitives(XmlPullParser xpp) throws XmlPullParserException,
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
				NumericRange range = XmlParser.parseNumericRange(xpp, TAG);
				if (range == null){
					Log.e(TAG, "Invalid numeric range for the primitive " + name);
					continue;
				}
				PrimitiveDef pd = new PrimitiveDef(name, range);
				_primitives.put(pd.getName(), pd);
			}
		}
	}
}

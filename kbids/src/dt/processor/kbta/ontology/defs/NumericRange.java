package dt.processor.kbta.ontology.defs;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import static dt.processor.kbta.ontology.OntologyLoader.*;
public class NumericRange{
	private final double _minValue;
	private final double _maxValue;
	private final boolean _isMaxE;
	private final boolean _isMinE;
	
	
	
	
	public NumericRange(double minValue, double maxValue, boolean isMinE, boolean isMaxE){
		this._minValue = minValue;
		this._maxValue = maxValue;
		this._isMaxE = isMaxE;
		this._isMinE = isMinE;
	}

	public static NumericRange ParseRange(XmlPullParser xpp){
		String minS;
		String maxS;
		boolean minE=true;
		boolean maxE=true;
		double min;
		double max;
		// Parsing the minimum value
		minS = xpp.getAttributeValue(null, "minE");
		if (minS == null) {
			minS = xpp.getAttributeValue(null, "min");
			minE = false;
		}
		if ("*".equals(minS)) {
			min = Double.NEGATIVE_INFINITY;
		} else {
			try {
				min = Double.parseDouble(minS);
			} catch (NumberFormatException e) {
				Log.e(TAG, "Erroneous minimum "
						+ "value for numeric range: ", e);
				return null;
			}
		}

		// Parsing the maximum value
		maxS = xpp.getAttributeValue(null, "maxE");
		if (maxS == null) {
			maxS = xpp.getAttributeValue(null, "max");
			maxE = false;
		}
		if ("*".equals(maxS)) {
			max = Double.POSITIVE_INFINITY;
		} else {
			try {
				max = Double.parseDouble(maxS);
			} catch (NumberFormatException e) {
				Log.e(TAG, "Erroneous maximum "
						+ "value for numeric range: ", e);
				return null;
			}
		}
		return new NumericRange(min, max, minE, maxE);
		
		
	}
	
	public double getMinValue(){
		return _minValue;
	}
	public double getMaxValue(){
		return _maxValue;
	}
	public boolean isMaxE(){
		return _isMaxE;
	}
	public boolean isMinE(){
		return _isMinE;
	}
	public boolean inRange(double value){
		return ((_isMinE) ? value >= _minValue : value > _minValue) 
			&& ((_isMaxE) ? value <= _maxValue : value < _maxValue);
	}
	
	@Override
	public String toString(){
		return " min= " + _minValue + " isMinE= "
		+ _isMinE + " max=" + _maxValue + " isMaxE= " + _isMaxE; 
	}
}

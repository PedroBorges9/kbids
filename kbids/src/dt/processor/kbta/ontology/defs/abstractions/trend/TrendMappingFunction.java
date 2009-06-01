package dt.processor.kbta.ontology.defs.abstractions.trend;

import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.util.TimeInterval;

public final class TrendMappingFunction{
	private final double _threshold;

	private final double _angle;

	private final long _maxGap;

	public TrendMappingFunction(double threshold, double angle, long maxGap){
		_threshold = threshold;
		_angle = angle;
		_maxGap = maxGap;
	}

	public boolean isGapSmallerThanMaxGap(TimeInterval t1, TimeInterval t2){
		return t1.getEndTime() - t2.getEndTime() <= _maxGap;
	}

	/**
	 * Checking whether the given primitive can be added to the given trend. That is, whether
	 * the angle between the vector (namely V) formed by the first and last primitives of the trend and
	 * the vector (namely U) formed by the new primitive and the last primitive of the trend is smaller
	 * than the angle from the ontology).
	 * 
	 * The calculation is done according to the formula: <tr>cosAlpha=(V*U)/(|V|*|U|)</tr><br>
	 * Note: V*U is the dot product
	 * 
	 * @return Whether the angle between U and V is smaller than the angle from the ontology
	 */
	public boolean isInIterpolationRange(Trend trend, Primitive primitive){
		Primitive first = trend.getFirst();
		Primitive last = trend.getLast();
		long tLast = last.getTimeInterval().getEndTime();
		long tFirst = first.getTimeInterval().getEndTime();
		long tNew = primitive.getTimeInterval().getEndTime();
		double vLast = last.getValue();
		double vFirst = first.getValue();
		double vNew = primitive.getValue();

		long dtLF = (tLast - tFirst)/1000;//to seconds
		long dtNL = (tNew - tLast)/1000;//to seconds
		double dvLF = vLast - vFirst;
		double dvNL = vNew - vLast;

		double cosAlpha = (dtLF * dtNL + dvLF * dvNL)
				/ Math.sqrt((dtLF * dtLF + dvLF * dvLF) * (dtNL * dtNL + dvNL * dvNL));
		double alpha = Math.toDegrees(Math.acos(cosAlpha));
	
//		System.out.println("("+dtLF+","+dvLF+")*"+"("+dtNL+","+dvNL+")");
//		System.out.println(cosAlpha);
//		System.out.println("alpha= "+alpha);
//		System.out.println("angle= "+_angle);
//		System.out.println("alpha < angle  "+(alpha <= _angle));
		
		return alpha <= _angle;

	}

	/*
	 * get two primitive and calculate change rate, and check 
	 * if (changeRate > threshold) then the value is "Increasing"
	 * if (changeRate < -threshold) then the value is "Decreasing"
	 * else then the value is "Same"
	 */
	public String mapValue(Primitive p1, Primitive p2){
		double changeRate = ((p2.getValue() - p1.getValue())
				/ (p2.getTimeInterval().getEndTime() - p1.getTimeInterval()
						.getEndTime()))*1000;

//		System.out.println("dtValue= "+(p2.getValue() - p1.getValue()));
//		System.out.println("dtTime= "+(p2.getTimeInterval().getEndTime() - p1.getTimeInterval()
//						.getEndTime()));
		

		
	//	System.out.println("changeRate= "+changeRate+" threshold= "+_threshold);
		if (changeRate > _threshold){
			return "Increasing";
		}else if (changeRate < -_threshold){
			return "Decreasing";
		}else{
			return "Same";
		}
	}

	@Override
	public String toString(){
		return "treshold= " + _threshold + " angle= " + _angle + " maxGap=" + _maxGap;
	}

}

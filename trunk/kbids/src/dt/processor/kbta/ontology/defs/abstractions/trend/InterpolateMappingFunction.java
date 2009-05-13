package dt.processor.kbta.ontology.defs.abstractions.trend;

import dt.processor.kbta.container.AllInstanceContainer;
import dt.processor.kbta.ontology.instances.Element;
import dt.processor.kbta.ontology.instances.Primitive;
import dt.processor.kbta.ontology.instances.Trend;
import dt.processor.kbta.util.TimeInterval;

public class InterpolateMappingFunction{
	private final double _treshold;

	private final double _angle;

	private final long _maxGap;

	public InterpolateMappingFunction(double treshold, double angle, long maxGap){
		_treshold = treshold;
		_angle = angle;
		_maxGap = maxGap;
	}

	public boolean mapElements(Trend currentTrend, Primitive primitive){
	

			if (primitive.getTimeInterval().getEndTime()
					- currentTrend.getTimeInterval().getEndTime() <= _maxGap){

				/*
				  if(check angle success){ 
				  	check treshold 
				  	
				  	currentTrend.setLast(primitive);
					currentTrend.getTimeInterval().setEndTime(primitive.getTimeInterval().getEndTime());
					currentTrend.setValue(checkTreshold(currentTrend.getFirst(), primitive));
					
				  }else{ create new Trend with
				  primitive and last in trend and enter to _trends }
				 */
			}else{
				// need to remove currentTrend ???
			}

		
		
		
	//	new Trend(currentTrend.getName(),"checkTreshold",new TimeInterval(currentTrend.getTimeInterval().getEndTime(),primitive.getTimeInterval().getEndTime()),)
		return true;
	}

	public String checkTreshold(Primitive first,Primitive last){
		long delta = (int)(last.getValue()-first.getValue())/(last.getTimeInterval().getEndTime()-first.getTimeInterval().getEndTime());
	
		if(delta>=-_treshold && delta<=_treshold){
			return "Same";
			
		}else if(delta<-_treshold){
			return "Decreasing";
		}else{
			return "Increasing";
		}
	}

	
	public long getMaxGap(){
		return _maxGap;
	}
	@Override
	public String toString(){
		return "treshold= " + _treshold + " angle= " + _angle + " maxGap=" + _maxGap;
	}

}

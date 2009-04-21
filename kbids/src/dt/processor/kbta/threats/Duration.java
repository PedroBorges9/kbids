package dt.processor.kbta.threats;

public class Duration {
	private final String _min;
	private final String _max;
	
	public Duration(String min,String max){
		_min = min;
		_max = max;
	}
	
	@Override
	public String toString() {
		
		return "min="+_min+" max="+_max;
	}
	
	
	
	

}
